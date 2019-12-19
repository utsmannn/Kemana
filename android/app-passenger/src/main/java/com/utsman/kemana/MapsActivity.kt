/*
 * Copyright (c) 2019 Muhammad Utsman
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

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.fragment.bottom_sheet.PickupBottomSheet
import com.utsman.kemana.impl.view.IMapView
import com.utsman.kemana.impl.view.IMessagingView
import com.utsman.kemana.maps_callback.PickupMaps
import com.utsman.kemana.maps_callback.ReadyMaps
import com.utsman.kemana.maps_callback.StartMaps
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.driver.Passenger
import com.utsman.kemana.remote.driver.Position
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses
import com.utsman.kemana.remote.toJSONObject
import com.utsman.kemana.remote.toOrderData
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONObject

class MapsActivity : RxAppCompatActivity(), IMapView, IMessagingView {

    private var mainBottomSheetFragment: MainBottomSheet? = null
    private var pickupBottomSheetFragment: PickupBottomSheet? = null

    private var mapsPresenter: MapsPresenter? = null
    private var messagingPresenter: MessagingPresenter? = null
    private var remotePresenter: RemotePresenter? = null

    private lateinit var startMaps: StartMaps
    private lateinit var readyMaps: ReadyMaps
    private lateinit var pickupMaps: PickupMaps

    private var latLng = LatLng()
    private var startLatLng = LatLng()
    private var destLatLng = LatLng()

    private var trackingDisposable: Disposable? = null
    private var cameraDisposable: Disposable? = null

    private val locationWatcher by lazy {
        LocationWatcher(this)
    }

    private val bottomSheet by lazy {
        val bottomSh = BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetUnDrag<*>
        bottomSh.setAllowUserDragging(false)
        return@lazy bottomSh
    }

    private val bottomDialog by lazy {
        val bottomDialog = BottomSheetDialog(this)
        val bottomDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_finding_order, null)
        bottomDialog.setContentView(bottomDialogView)
        bottomDialog.setCancelable(false)

        return@lazy bottomDialog
    }

    private val passenger by lazy {
        getBundleFrom<Passenger>("passenger")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_maps)
        bottomSheet.hidden()
        mapbox_view.onCreate(savedInstanceState)

        locationWatcher.getLocation(this) { location ->
            init(location)
        }

    }

    private fun init(location: Location) {
        latLng = location.toLatLngMapbox()

        logi("location ready --> $latLng")

        mapsPresenter = MapsPresenter(this)
        messagingPresenter = MessagingPresenter(this)

        remotePresenter = RemotePresenter(composite)
        passenger?.position = Position(latLng.latitude, latLng.longitude)
        mapsPresenter?.mapStart(latLng)

        if (mapsPresenter != null && messagingPresenter != null) {
            mainBottomSheetFragment = MainBottomSheet(mapsPresenter!!, messagingPresenter!!, latLng)
        }

        mainBottomSheetFragment?.pricingGone()

        replaceFragment(mainBottomSheetFragment, R.id.main_frame_bottom_sheet)

        bottomSheet.collapse()
    }

    override fun mapStart(startLatLng: LatLng) {
        startMaps = StartMaps(composite, this, startLatLng) { map, marker ->
            // map ready from invoke

        }

        mapbox_view.getMapAsync(startMaps)
        startMaps.setPaddingBottom(200)
        composite.add(startMaps)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        startLatLng = LatLng(start.geometry!![0]!!, start.geometry!![1]!!)
        destLatLng = LatLng(destination.geometry!![0]!!, destination.geometry!![1]!!)

        logi("poly is --> ${polyline?.geometry}")

        if (polyline == null) {
            toast("failed")

            mapbox_view.getMapAsync(startMaps)
            startMaps.setPaddingBottom(200)

        } else {
            readyMaps = ReadyMaps(this, startLatLng, destLatLng, polyline.geometry)

            mainBottomSheetFragment?.pricingVisible()
            mapbox_view.getMapAsync(readyMaps)
            readyMaps.setPaddingBottom(200)
            composite.add(readyMaps)
        }
    }

    override fun mapPickup(orderData: OrderData) {
        bottomDialog.cancel()

        logi("driver id is --> ${orderData.attribute.driver?.id}")
        messagingPresenter?.let {
            pickupBottomSheetFragment = PickupBottomSheet(orderData, it)
        }

        pickupMaps = PickupMaps(this, composite, orderData) { mapbox, marker, camDis ->
            replaceFragment(pickupBottomSheetFragment, R.id.main_frame_bottom_sheet)

            cameraDisposable = camDis
            /*trackingDisposable = Rabbit.getInstance()?.listen { from, body ->
                logi("listen from $from - for traking -> $body")
                val type = body.getInt("type")

                when (type) {
                    Type.TRACKING -> {
                        val data = body.getJSONObject("data")

                        logi("driver: $from location is --> $data")
                        val newLat = data.getDouble("lat")
                        val newLon = data.getDouble("lon")
                        val newLatLong = LatLng(newLat, newLon)

                        marker?.moveMarkerSmoothly(newLatLong)
                    }
                }
            }*/

            //composite.addAll(cameraDisposable, trackingDisposable)
            composite.addAll(cameraDisposable)
        }

        mapbox_view.getMapAsync(pickupMaps)
        composite.add(pickupMaps)
    }

    override fun failedServerConnection() {
        val bottomDialog = BottomSheetDialog(this)
        bottomDialog.setContentView(R.layout.bottom_dialog_error)
        bottomDialog.show()
    }

    override fun dispose() {
        trackingDisposable?.dispose()
        cameraDisposable?.dispose()

        readyMaps.dispose()
        startMaps.dispose()
        pickupMaps.dispose()
    }

    override fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        bottomDialog.show()

        remotePresenter?.getDriversActiveEmail {  emails ->
            if (!emails.isNullOrEmpty()) {

                val size = emails.size-1
                var target = 0

                if (target <= size) {
                    finder(emails[target], startPlaces, destPlaces, polyline)
                } else {
                    bottomDialog.dismiss()
                }

                // listen callback driver
                Rabbit.getInstance()?.listen { from, body ->
                    logi("confirm driver coming -> from $from is $body")

                    val type = body.getInt("type")
                    val data = body.getJSONObject("data")

                    when (type) {
                        Type.ORDER_CONFIRM -> {
                            val orderData = data.toOrderData()
                            if (orderData.accepted) {
                                logi("driver id is --> ${orderData.attribute.driver?.id}")
                                setupOrderAccepted(orderData)

                            } else {
                                logi("order rejected")

                                if (target < size) {
                                    logi("target is $target and size is $size")
                                    target = (+1).apply {
                                        finder(emails[this], startPlaces, destPlaces, polyline)
                                    }
                                } else {
                                    toast("cannot driver accepted, please try again")
                                    bottomDialog.cancel()
                                }
                            }
                        }
                    }
                }

            } else {
                toast("driver not found")
                bottomDialog.dismiss()
            }
        }
    }

    override fun orderCancel() {
        mapsPresenter?.dispose()

        locationWatcher.getLocation(this) { location ->
            init(location)
        }
    }

    private fun finder(email: String, startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        val jsonBody = JSONObject()
        jsonBody.apply {
            put("person", passenger?.toJSONObject())
            put("start", startPlaces.placeName)
            put("destination", destPlaces.placeName)
            put("startPlace", startPlaces.toJSONObject())
            put("destPlace", destPlaces.toJSONObject())
            put("distance", polyline.distance)
        }

        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("type", Type.ORDER_REQUEST)
            put("data", jsonBody)
        }

        logi("start send to $email")
        Rabbit.getInstance()?.publishTo(email,  jsonRequest) {
            bottomDialog.dismiss()
            toast("try again -> ${it.localizedMessage}")
        }
    }

    private fun setupOrderAccepted(orderData: OrderData) {
        logi("order accepted")

        val driver = orderData.attribute.driver
        logi("driver id is --> ${driver?.id}")
        mapsPresenter?.mapOrder(orderData)
    }

    override fun onDestroy() {
        mapbox_view.onDestroy()
        super.onDestroy()
    }

    override fun onStart() {
        mapbox_view.onStart()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapbox_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapbox_view.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapbox_view.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapbox_view.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapbox_view.onLowMemory()
    }
}