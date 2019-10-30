package com.utsman.kemana.driver

import android.content.Intent
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.kemana.auth.EventUser
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.auth.userToString
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.RxAppCompatActivity
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.preferences
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.getLocation
import kotlinx.android.synthetic.main.activity_map.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapActivity : RxAppCompatActivity() {

    private lateinit var userDriver: User
    private lateinit var mapsCallback: MapsCallback
    private lateinit var backendlessApp: BackendlessApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, Key.MAP_KEY)
        setContentView(R.layout.activity_map)
        val intentService = Intent(this, MapsServiceLocator::class.java)

        backendlessApp = BackendlessApp(application, compositeDisposable)
        userDriver = (intent.getStringExtra("user") ?: "").stringToUser()

        getLocation(compositeDisposable, false) { loc ->
            mapsCallback = MapsCallback(this, loc) {
                startService(intentService)
            }

            map_view.getMapAsync(mapsCallback)
        }

        switch_active.setOnCheckedChangeListener { compoundButton, b ->
            logi("save val i --> $b")
            if (b) {
                trackingOn()
            } else {
                trackingOff()
            }
        }
    }

    private fun trackingOn() {

        val token = preferences("account").getString("token", "token") ?: "null-token"

        logi("user email --> ${userDriver.email}")
        logi("anjaylah --> $userDriver")

        val newUser = User(
            userId = userDriver.userId,
            name = userDriver.name,
            email = userDriver.email,
            vehiclesType = userDriver.vehiclesType,
            vehiclesPlat = userDriver.vehiclesPlat
        )

        userDriver.token = null
        backendlessApp.saveUserToType(token, "driver_active", newUser, {
            logi("saving table success with resp -> $it")

            preferences("user").edit().putString("model-active", it.userToString()).apply()
            EventBus.getDefault().post(EventUser(true))
        })
    }

    private fun trackingOff() {
        EventBus.getDefault().post(EventUser(false))
        val token = preferences("account").getString("token", "token") ?: "null-token"
        val userActiveString = preferences("user").getString("model-active", "") ?: "no"
        val objectIdActive = userActiveString.stringToUser().objectId ?: "nn"
        backendlessApp.deleteDriverActive(objectIdActive, token, "driver_active", {
            logi("delete success")
        })
    }

    @Subscribe
    fun onTrackingUpdate(eventTracking: EventTracking) {
        mapsCallback.onEventTracker(eventTracking)
    }

    override fun onStart() {
        map_view.onStart()
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        map_view.onStop()
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        map_view.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map_view.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}