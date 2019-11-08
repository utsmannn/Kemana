package com.utsman.kemana.driver.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.EventUser
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.auth.toUser
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.ext.loge
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.base.ext.preferences
import com.utsman.kemana.driver.event.EventPassengerConfirm
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.LatLngUpdater
import com.utsman.kemana.message.EventOrderData
import com.utsman.kemana.message.toOrderData
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection
import com.utsman.smartmarker.SmartLatLon
import com.utsman.smartmarker.SmartUtil
import com.utsman.smartmarker.location.LocationUpdateListener
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapsServiceLocator : Service() {
    private lateinit var currentLatLng: LatLng
    private lateinit var backendlessApp: BackendlessApp

    private val disposable = CompositeDisposable()
    private val liveRotate = MutableLiveData<Float>()
    private var trackingActive = false

    private var rmqaConnection: RmqaConnection? = null
    private var user: User? = null


    private val locationWatcher by lazy {
        LocationWatcher(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        liveRotate.postValue(0f)
        backendlessApp = BackendlessApp(application, disposable)

        rmqaConnection = RmqaConnection.Builder(this)
            .setServer("woodpecker.rmq.cloudamqp.com")
            .setUsername("edafafqh")
            .setPassword("ypJNO0725gpmo1tFnr4cbyFThZ1ZwMLH")
            .setVhost("edafafqh")
            .setExchangeName("kemana")
            .setConnectionName("kemana")
            .setRoutingKey("route_kemana")
            .setAutoClearQueue(true)
            .build()

        locationWatcher.getLocationUpdate(object : LocationUpdateListener {
            override fun newLocation(newLocation: Location) {
                val latLngUpdater = LatLngUpdater(currentLatLng, newLocation.toLatLngMapbox())

                val angle = SmartUtil.getAngle(
                    SmartLatLon(currentLatLng.latitude, currentLatLng.longitude),
                    SmartLatLon(newLocation.latitude, newLocation.longitude)
                ).toFloat()

                val eventTracking = EventTracking(latLngUpdater)
                EventBus.getDefault().post(eventTracking)

                val token = preferences("account").getString("token", "token") ?: "null-token"

                if (trackingActive) {
                    user?.let { usr ->
                        usr.angle = angle.toDouble()
                        usr.lon = newLocation.longitude
                        usr.lat = newLocation.latitude
                        backendlessApp.updateDriverLocation("driver_active", usr.objectId!!, usr, token, {
                            logi("update success")
                        }, {
                            loge("update fail --> ${it?.message}")
                        })
                    }
                }

                logi("ppp --> event is --> $trackingActive --> ${user?.objectId}")
            }

            override fun failed(throwable: Throwable?) {

            }

            override fun oldLocation(oldLocation: Location) {
                currentLatLng = oldLocation.toLatLngMapbox()
            }
        })

        val queueName = preferences("account").getString("user-id", "user")
        Rmqa.connect(rmqaConnection, queueName, Rmqa.TYPE.DIRECT) { senderId, data ->
            logi("connection to user --> $data")

            val status = data.getString("status")

            if (status == "finding") {
                val orderData = data.getJSONObject("data")
                EventBus.getDefault().post(EventOrderData(orderData.toOrderData()))
            }

            if (status == "passenger_confirm") {
                logi("passenger is confirm")
                val passengerData = data.getJSONObject("data")
                EventBus.getDefault().post(EventPassengerConfirm(passengerData.toUser()))
            }
        }

        return START_STICKY
    }

    @Subscribe
    fun onEventTrackingOn(eventUser: EventUser) {
        user = eventUser.userString?.stringToUser()
        trackingActive = eventUser.tracking
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        disposable.dispose()
        EventBus.getDefault().post(EventUser(false, null))
        val token = preferences("account").getString("token", "token") ?: "null-token"
        val userActiveString = preferences("user").getString("model-active", "") ?: "no"
        val objectIdActive = userActiveString.stringToUser().objectId ?: "nn"
        backendlessApp.deleteDriverActive(objectIdActive, token, "driver_active", {
            logi("delete success")
        })
        super.onDestroy()
    }
}