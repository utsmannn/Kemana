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

package com.utsman.kemana

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.stringToUser
import com.utsman.kemana.auth.toJSONObject
import com.utsman.kemana.auth.toUser
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.ext.ProgressHelper
import com.utsman.kemana.base.ext.collapse
import com.utsman.kemana.base.ext.dp
import com.utsman.kemana.base.ext.dpFloat
import com.utsman.kemana.base.ext.expand
import com.utsman.kemana.base.ext.hidden
import com.utsman.kemana.base.ext.isExpand
import com.utsman.kemana.base.ext.isHidden
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.base.ext.replaceFragment
import com.utsman.kemana.base.ext.toast
import com.utsman.kemana.base.rx.RxAppCompatActivity
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.OrderBottomFragment
import com.utsman.kemana.fragment.StartBottomFragment
import com.utsman.kemana.fragment.callback.CallbackFragment
import com.utsman.kemana.fragment.callback.CallbackFragmentOrder
import com.utsman.kemana.fragment.callback.CallbackFragmentStart
import com.utsman.kemana.maps.MapsOrder
import com.utsman.kemana.maps.MapsStart
import com.utsman.kemana.maps.MapsWithDriver
import com.utsman.kemana.message.OrderData
import com.utsman.kemana.message.toJSONObject
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.json.JSONObject

class MapsActivity : RxAppCompatActivity() {

    private lateinit var bottomStart: StartBottomFragment
    private lateinit var bottomOrder: OrderBottomFragment
    private lateinit var userPassenger: User
    private lateinit var orderData: OrderData

    private var fromLatLng: LatLng? = null
    private var toLatLng: LatLng? = null
    private var onOrder = false
    private var rmqaConnection: RmqaConnection? = null

    private var dialog: Dialog? = null
    private var position = 0
    private var finderStatus = false
    private var markerDriver: Marker? = null

    private val progressHelper by lazy { ProgressHelper(this) }
    private val bottomSheetLayout by lazy {
        BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetUnDrag<*>
    }

    private val locationWatcher by lazy {
        LocationWatcher(this)
    }

    private val callbackFragment = object : CallbackFragment {
        override fun onCollapse() {
            bottomSheetLayout.collapse()
        }

        override fun onExpand() {
            bottomSheetLayout.expand()
        }

        override fun onHidden() {
            bottomSheetLayout.hidden()
        }

        override fun onOrder(order: Boolean) {
            onOrder = order
            map_view.invalidate()

            if (fromLatLng != null && toLatLng != null) {

                val mapsOrder = MapsOrder(this@MapsActivity, compositeDisposable) { route ->
                    bottomSheetLayout.peekHeight = dp(280)
                    if (fromLatLng != null && toLatLng != null) {
                        bottomOrder.setFromLatLng(fromLatLng!!)
                        bottomOrder.setToLatLng(toLatLng!!)
                        bottomOrder.setDistance(route.routes[0].distance)
                    }
                    replaceFragment(bottomOrder, R.id.main_frame_bottom)
                }
                mapsOrder.setFromLatLng(fromLatLng!!)
                mapsOrder.setToLatLng(toLatLng!!)
                mapsOrder.setPaddingBottom(dp(280))

                Handler().postDelayed({
                    map_view.getMapAsync(mapsOrder)
                }, 200)
            } else if (bottomSheetLayout.isHidden() && fromLatLng == null || toLatLng == null) {
                bottomSheetLayout.collapse()
            }
        }
    }

    private val callbackFragmentStart = object : CallbackFragmentStart {
        override fun fromLatLng(latLng: LatLng) {
            userPassenger.lat = latLng.latitude
            userPassenger.lon = latLng.longitude
            fromLatLng = latLng
        }

        override fun toLatLng(latLng: LatLng) {
            toLatLng = latLng
        }
    }

    private val callbackFragmentOrder = object : CallbackFragmentOrder {
        override fun onBtnOrderPress(listDriver: List<User?>, i: Int, distance: Double) {

            if (dialog != null) {
                dialog!!.show()
            }

            if (i < listDriver.size) {
                finder(distance, listDriver, i)
            } else {

                if (dialog != null) {
                    dialog!!.dismiss()
                }
            }
        }

        override fun onBtnBackPress() {
            onBackPressed()
        }
    }

    private fun finder(distance: Double, listDriver: List<User?>, i: Int) {
        orderData = OrderData (
            userPassenger.userId,
            userPassenger.name,
            userPassenger.photoProfile!!,
            fromLatLng?.latitude!!,
            fromLatLng?.longitude!!,
            toLatLng?.latitude!!,
            toLatLng?.longitude!!,
            distance
        )

        if (!listDriver.isNullOrEmpty()) {
            try {
                val findingData = JSONObject()
                findingData.put("status", "finding")
                findingData.put("data", orderData.toJSONObject())

                Rmqa.publishTo(listDriver[i]!!.userId, orderData.userId, findingData)
                finderStatus = true
            } catch (e: IndexOutOfBoundsException) {
                toast("cannot finding ojek --> ${e.printStackTrace()}")
                if (dialog != null) {
                    dialog!!.dismiss()
                }
            }
        } else {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, Key.MAP_KEY)
        setContentView(R.layout.activity_map)
        map_view.onCreate(savedInstanceState)

        setupVariable()
        messageConnection()
        userStarted()

        bottomSheetLayout.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, slide: Float) {

                if (slide == 1f) {
                    main_bottom_sheet.radius = dpFloat(0)
                } else {
                    main_bottom_sheet.radius = dpFloat(16)
                }
            }

            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED && bottomStart.isVisible) {
                    bottomStart.clearFocus()
                }
            }
        })
    }

    private fun messageConnection() {
        Rmqa.connect(rmqaConnection, userPassenger.userId, Rmqa.TYPE.DIRECT) { senderId, jsonObject ->
            val jsonData = jsonObject.getString("status")

            if (jsonData == "accepted" && finderStatus) {
                val data = jsonObject.getJSONObject("driver").toUser()
                if (dialog != null) {
                    dialog!!.dismiss()

                    // accept here
                    logi(data.toString())
                    logi("user --> $userPassenger")
                    setupMapWithDriver(data)
                }
            } else {
                position += 1
                Handler().postDelayed({
                    bottomOrder.startOrder(position)
                }, 800)
            }

            if (jsonData == "tracking") {
                val lat = jsonObject.getDouble("lat")
                val lon = jsonObject.getDouble("lon")
                val newLatLng = LatLng(lat, lon)

                markerDriver?.moveMarkerSmoothly(newLatLng)
            }
        }
    }

    private fun setupMapWithDriver(data: User) {
        val mapWithDriver = MapsWithDriver(this, compositeDisposable, data, userPassenger) { markerDriver ->

            val dataConfirm = JSONObject()
            dataConfirm.put("status", "passenger_confirm")
            dataConfirm.put("data", userPassenger.toJSONObject())

            Rmqa.publishTo(data.userId, orderData.userId, dataConfirm)

            this.markerDriver = markerDriver
        }
        mapWithDriver.setPaddingBottom(dp(280))

        map_view.getMapAsync(mapWithDriver)
    }

    private fun setupVariable() {
        bottomSheetLayout.setAllowUserDragging(false)
        userPassenger = (intent.getStringExtra("user") ?: "").stringToUser()
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

        dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_finding_driver)
            setCancelable(true)
            setOnDismissListener {
                finderStatus = false
            }
        }

        bottomStart = StartBottomFragment(callbackFragment, callbackFragmentStart)
        bottomOrder = OrderBottomFragment(callbackFragment, callbackFragmentOrder)
    }

    private fun userStarted() {
        bottomSheetLayout.hidden()
        val mapStart = MapsStart(this, compositeDisposable) {
            bottomSheetLayout.peekHeight = dp(200)
            onOrder = false
            position = 0
            replaceFragment(bottomStart, R.id.main_frame_bottom)
        }
        mapStart.setPaddingBottom(dp(200))
        progressHelper.showProgressDialog()

        if (fromLatLng != null && toLatLng != null) {
            progressHelper.hideProgressDialog()
            map_view.getMapAsync(mapStart)
            mapStart.setCurrentLatLng(fromLatLng!!)
            bottomStart.setCurrentLatLng(fromLatLng!!)
        } else {

            locationWatcher.getLocation(this) { location ->
                progressHelper.hideProgressDialog()
                map_view.getMapAsync(mapStart)
                mapStart.setCurrentLatLng(location.toLatLngMapbox())
                bottomStart.setCurrentLatLng(location.toLatLngMapbox())
            }
        }
    }

    override fun onBackPressed() {
        when {
            onOrder -> userStarted()
            bottomSheetLayout.isExpand() -> bottomSheetLayout.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
        locationWatcher.stopLocationWatcher()
        Rmqa.disconnect(rmqaConnection)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}