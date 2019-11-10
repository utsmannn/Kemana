/*
 * Copyright 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.EventUser
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.auth.toJSONObject
import com.utsman.kemana.auth.userToString
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.ext.calculateDistanceKm
import com.utsman.kemana.base.ext.calculatePricing
import com.utsman.kemana.base.ext.collapse
import com.utsman.kemana.base.ext.hidden
import com.utsman.kemana.base.ext.loadCircleUrl
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.base.ext.preferences
import com.utsman.kemana.base.ext.replaceFragment
import com.utsman.kemana.base.rx.RxAppCompatActivity
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.driver.event.EventPassengerConfirm
import com.utsman.kemana.driver.fragment.PickupFragment
import com.utsman.kemana.driver.maps.MapsMain
import com.utsman.kemana.driver.maps.MapsPickup
import com.utsman.kemana.driver.service.MapsServiceLocator
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.toLocation
import com.utsman.kemana.message.EventOrderData
import com.utsman.kemana.places.PlaceRouteApp
import com.utsman.rmqa.Rmqa
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.dialog_offering.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class MapsActivity : RxAppCompatActivity() {

    private val MAIN_MAP = "main_map"
    private val PICKUP_MAP = "pickup_map"

    private lateinit var userDriver: User
    private lateinit var mainMaps: MapsMain
    private lateinit var pickupMaps: MapsPickup
    private lateinit var backendlessApp: BackendlessApp
    private var mapActive = MAIN_MAP

    private val intentService by lazy {
        Intent(this, MapsServiceLocator::class.java)
    }

    private val bottomSheetLayout by lazy {
        BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetUnDrag<*>
    }

    private val locationWatcher by lazy {
        LocationWatcher(this)
    }

    //private var pickupFragment: PickupFragment? = null
    private lateinit var pickupFragment: PickupFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, Key.MAP_KEY)
        setContentView(R.layout.activity_map)

        bottomSheetLayout.hidden()

        backendlessApp = BackendlessApp(application, compositeDisposable)
        userDriver = (intent.getStringExtra("user") ?: "").stringToUser()

        locationWatcher.getLocation(this) { loc ->
            mainMaps = MapsMain(this, loc.toLatLngMapbox()) { marker ->
                startService(intentService)
                mapActive = MAIN_MAP
            }
            map_view.getMapAsync(mainMaps)
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

        userDriver.token = null
        backendlessApp.saveUserToType(token, "driver_active", userDriver, {
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
        userDriver.lat = eventTracking.latLngUpdater.newLatLng.latitude
        userDriver.lon = eventTracking.latLngUpdater.newLatLng.longitude

        if (mapActive == MAIN_MAP) {
            try {
                mainMaps.onEventTracker(eventTracking)
            } catch (e: Error) {
                e.printStackTrace()
            }
        } else {
            try {
                pickupMaps.onEventTracker(eventTracking)
            } catch (e: Error) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("InflateParams")
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

            text_user_customer.text = orderData.username
            text_pricing.text = price
            text_distance.text = distanceKm

            img_user_customer.loadCircleUrl(orderData.userImg)

            btn_order_reject.setOnClickListener {
                dialogBuilder.dismiss()
                userDriver.onOrder = false

                val data = JSONObject()
                data.put("confirm", false)

                Rmqa.publishTo(orderData.userId, userDriver.userId, data)
            }

            btn_order_accept.setOnClickListener {
                dialogBuilder.dismiss()
                pickupFragment = PickupFragment(orderData)
                userDriver.onOrder = true
                logi("aa --> ${userDriver.lat} --> ${userDriver.toJSONObject()}")

                val data = JSONObject()
                data.put("status", "accepted")
                data.put("driver", userDriver.toJSONObject())

                Rmqa.publishTo(orderData.userId, userDriver.userId, data)

            }
        }

        if (!dialogBuilder.isShowing) {
            dialogBuilder.show()
        }
    }

    @Subscribe
    fun onPassengerConfirm(passengerConfirm: EventPassengerConfirm) {
        logi("passenger confirm map update")

        pickupMaps = MapsPickup(this, userDriver, passengerConfirm.passengerData, compositeDisposable) {
            //pickupFragment.setDistance(it.routes[0].distance)
            replaceFragment(pickupFragment, R.id.main_frame_bottom)
            bottomSheetLayout.collapse()
        }
        pickupMaps.setPaddingBottom(250)
        map_view.getMapAsync(pickupMaps)
        mapActive = PICKUP_MAP
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
        locationWatcher.stopLocationWatcher()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map_view.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}