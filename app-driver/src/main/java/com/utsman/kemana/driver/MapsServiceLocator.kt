package com.utsman.kemana.driver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.EventUser
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


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        liveRotate.postValue(0f)
        backendlessApp = BackendlessApp(application, disposable)

        getLocationDebounce(disposable, { oldLocation ->
            currentLatLng = oldLocation.toLatlng()
        }, { newLocation ->
            val latLngUpdater = LatLngUpdater(currentLatLng, newLocation.toLatlng())

            markerUtil = MarkerUtil(this, currentLatLng)

            val angle = markerUtil.getAngle(currentLatLng, newLocation.toLatlng()).toFloat()
            liveRotate.postValue(angle)

            val userPref = preferences("user").getString("model-active", "") ?: ""
            val user = userPref.stringToUser()

            liveRotate.observeForever { rotation ->
                val eventTracking = EventTracking(latLngUpdater)
                EventBus.getDefault().post(eventTracking)

                val token = preferences("account").getString("token", "token") ?: "null-token"

                if (trackingActive) {
                    try {
                        user.angle = rotation.toDouble()
                        user.lon = newLocation.longitude
                        user.lat = newLocation.latitude
                        backendlessApp.updateDriverLocation("driver_active", user.objectId!!, user, token, {
                            logi("update success")
                        }, {
                            loge("update fail --> ${it?.message}")
                        })
                    } catch (e: NullPointerException) {
                        loge(e.message)
                        e.printStackTrace()
                    }
                }

                logi("ppp --> event is --> $trackingActive")
            }

        })

        return super.onStartCommand(intent, flags, startId)
    }

    @Subscribe
    fun onEventTrackingOn(eventUser: EventUser) {
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