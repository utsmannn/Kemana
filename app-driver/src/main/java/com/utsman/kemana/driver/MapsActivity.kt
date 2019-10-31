package com.utsman.kemana.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.EventUser
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.auth.toJSONObject
import com.utsman.kemana.auth.userToString
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.RxAppCompatActivity
import com.utsman.kemana.base.calculateDistanceKm
import com.utsman.kemana.base.calculatePricing
import com.utsman.kemana.base.loadCircleUrl
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.preferences
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.getLocation
import com.utsman.kemana.maputil.toLocation
import com.utsman.kemana.message.EventOrderData
import com.utsman.kemana.places.PlaceRouteApp
import com.utsman.rmqa.Rmqa
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.dialog_offering.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MapsActivity : RxAppCompatActivity() {

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

        setupTopView(userDriver)

        switch_active.setOnCheckedChangeListener { compoundButton, b ->
            logi("save val i --> $b")
            if (b) {
                trackingOn()
            } else {
                trackingOff()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTopView(userDriver: User) {
        img_user.loadCircleUrl(userDriver.photoProfile)
        text_name_user.text = userDriver.name
        text_vehicles_user.text = "${userDriver.vehiclesType} ${userDriver.vehiclesPlat}"
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
            val evenUser = EventUser(true, it.userToString())
            EventBus.getDefault().post(evenUser)
        })
    }

    private fun trackingOff() {
        EventBus.getDefault().post(EventUser(false, null))
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

    @Subscribe
    fun onOrderComing(eventOrderData: EventOrderData) {

        val viewDialog = layoutInflater.inflate(R.layout.dialog_offering, null)
        val dialogOrderBuilder = AlertDialog.Builder(this).apply {
            setView(viewDialog)
        }

        val dialogBuilder = dialogOrderBuilder.create()

        viewDialog.apply {
            val orderData = eventOrderData.orderData

            val fromLatLng = LatLng(orderData.fromLat, orderData.fromLng)
            val toLatLng = LatLng(orderData.toLat, orderData.toLng)
            val placeRouteApp = PlaceRouteApp(compositeDisposable)
            val price = orderData.distance.calculatePricing()
            val distanceKm = orderData.distance.calculateDistanceKm()

            placeRouteApp.getMyAddress(fromLatLng.toLocation())
                .observe(this@MapsActivity, Observer {
                    val addressName = it.place_name
                    text_from_location.text = addressName
                })

            placeRouteApp.getMyAddress(toLatLng.toLocation())
                .observe(this@MapsActivity, Observer {
                    val addressName = it.place_name
                    text_to_location.text = addressName
                })

            val userString = preferences("user").getString("model-active", "") ?: "null"
            val user = userString.stringToUser()

            text_user_customer.text = orderData.username
            text_pricing.text = price
            text_distance.text = distanceKm

            img_user_customer.loadCircleUrl(orderData.userImg)

            btn_order_reject.setOnClickListener {
                dialogBuilder.dismiss()
                user.onOrder = false
                Rmqa.publishTo(orderData.userId, user.userId, user.toJSONObject())
            }

            btn_order_accept.setOnClickListener {
                dialogBuilder.dismiss()
                user.onOrder = true
                Rmqa.publishTo(orderData.userId, user.userId, user.toJSONObject())
            }
        }


        if (!dialogBuilder.isShowing) {
            dialogBuilder.show()
        }
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