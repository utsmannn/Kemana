package com.utsman.kemana.driver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.EventUser
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.preferences
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.LatLngUpdater
import com.utsman.kemana.maputil.MarkerUtil
import com.utsman.kemana.maputil.getLocationDebounce
import com.utsman.kemana.maputil.toLatlng
import com.utsman.kemana.message.EventOrderData
import com.utsman.kemana.message.toOrderData
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapsServiceLocator : Service() {
    private lateinit var currentLatLng: LatLng
    private lateinit var markerUtil: MarkerUtil
    private lateinit var backendlessApp: BackendlessApp

    private val disposable = CompositeDisposable()
    private val liveRotate = MutableLiveData<Float>()
    private var trackingActive = false

    private var rmqaConnection: RmqaConnection? = null
    private var user: User? = null

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

        getLocationDebounce(disposable, { oldLocation ->
            currentLatLng = oldLocation.toLatlng()
        }, { newLocation ->
            val latLngUpdater = LatLngUpdater(currentLatLng, newLocation.toLatlng())

            markerUtil = MarkerUtil(this, currentLatLng)

            val angle = markerUtil.getAngle(currentLatLng, newLocation.toLatlng()).toFloat()
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
        })

        val queueName = preferences("account").getString("user-id", "user")
        Rmqa.connect(rmqaConnection, queueName, Rmqa.TYPE.DIRECT) { senderId, data ->
            EventBus.getDefault().post(EventOrderData(data.toOrderData()))
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
        super.onDestroy()
    }
}