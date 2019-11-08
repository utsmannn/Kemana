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

package com.utsman.kemana.driver.maps

import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.auth.User
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.EventTracking
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection
import com.utsman.smartmarker.mapbox.MarkerLayer
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import org.json.JSONObject

class MapsPickup(
    private val activity: FragmentActivity,
    private val driver: User,
    private val user: User,
    private val ready: (driver: User) -> Unit
) : OnMapReadyCallback {

    private var paddingBottom = 0
    private lateinit var markerLayer: MarkerLayer

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    private val trackerConnection by lazy {
        RmqaConnection.Builder(activity)
            .setServer("orangutan.rmq.cloudamqp.com")
            .setUsername("wtivlbpg")
            .setPassword("Iy9YuSSjFqPX9aBYH0A7dzA67ViJrWiv")
            .setVhost("wtivlbpg")
            .setExchangeName("exchange_tracker")
            .setConnectionName("connection_tracker")
            .setRoutingKey("route_key")
            .setAutoClearQueue(true) // By default it is `false`, when the connection is closed, the queue will be cleared
            .build()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            val driverLatLng = LatLng(driver.lat!!, driver.lon!!)
            val passengerLatLng = LatLng(user.lat!!, user.lon!!)

            val position = LatLngBounds.Builder()
                .include(driverLatLng)
                .include(passengerLatLng)
                .build()

            val markerOptionDriver = MarkerOptions.Builder()
                .addPosition(driverLatLng)
                .addIcon(R.drawable.ic_marker_driver, true)
                .setId("driver")
                .build(activity)

            val markerOptionsPassenger = MarkerOptions.Builder()
                .addPosition(passengerLatLng)
                .addIcon(R.drawable.ic_person_location, true)
                .setId("passenger")
                .build(activity)

            ready.invoke(driver)

            markerLayer = mapboxMap.addMarker(markerOptionDriver, markerOptionsPassenger)

            val cameraPosition = CameraUpdateFactory.newLatLngBounds(
                position, 200, 200, 200, paddingBottom + 200)
            mapboxMap.animateCamera(cameraPosition)
        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        val markerDriver = markerLayer.get("driver")
        markerDriver?.moveMarkerSmoothly(eventTracking.latLngUpdater.newLatLng)

        val jsonTracker = JSONObject()
        jsonTracker.put("status", "tracking")
        jsonTracker.put("lat", eventTracking.latLngUpdater.newLatLng.latitude)
        jsonTracker.put("lon", eventTracking.latLngUpdater.newLatLng.longitude)

        Rmqa.publishTo(user.userId, driver.userId, jsonTracker)
    }
}