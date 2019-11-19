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

import android.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CannotAddLayerException
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.CannotAddSourceException
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils
import com.utsman.kemana.auth.User
import com.utsman.kemana.base.ext.loge
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.places.PlaceRouteApp
import com.utsman.kemana.places.Route
import com.utsman.rmqa.Rmqa
import com.utsman.smartmarker.mapbox.MarkerLayer
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject

class MapsPickup(
    private val activity: FragmentActivity,
    private val driver: User,
    private val user: User,
    private val disposable: CompositeDisposable,
    private val ready: (route: Route) -> Unit
) : OnMapReadyCallback {

    private var paddingBottom = 0
    private lateinit var markerLayer: MarkerLayer

    private val placeRouteApp = PlaceRouteApp(disposable)

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
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

            val cameraPosition = CameraUpdateFactory.newLatLngBounds(
                position, 200, 200, 200, paddingBottom + 200)
            mapboxMap.animateCamera(cameraPosition)

            val bodyString = "coordinates=" +
                "${driverLatLng.longitude}," +
                "${driverLatLng.latitude};" +
                "${passengerLatLng.longitude}," +
                "${passengerLatLng.latitude}"

            placeRouteApp.getRoute(bodyString).observe(activity, Observer {
                it?.let { route ->
                    ready.invoke(route)
                    getRouteLine(driverLatLng, passengerLatLng, route, mapboxMap, style)
                    markerLayer = mapboxMap.addMarker(markerOptionDriver, markerOptionsPassenger)
                }
            })
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

    private fun getRouteLine(fromLatLng: LatLng, toLatLng: LatLng, route: Route, mapboxMap: MapboxMap, style: Style) {
        val geometry = route.routes[0].geometry
        val id = "source-route"

        logi(route.routes[0].geometry)

        val lineString = LineString.fromPolyline(geometry, 5)
        val featureRoute = Feature.fromGeometry(lineString)
        val sourceRoute = GeoJsonSource(id, featureRoute)

        val latLngBounds = LatLngBounds.Builder()
            .include(LatLng(route.waypoints[0].location[1], route.waypoints[0].location[0]))
            .include(LatLng(route.waypoints[1].location[1], route.waypoints[1].location[0]))
            .include(fromLatLng)
            .include(toLatLng)
            .build()

        mapboxMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                200, 200, 200, paddingBottom + 200
            )
        )

        try {
            style.addSource(sourceRoute)
        } catch (e: IllegalStateException) {
            logi("tai")
        } catch (e: CannotAddSourceException) {
            loge("anjay ada source")
        }

        val lineLayer = LineLayer(id, id).apply {
            withProperties(
                PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.parseColor("#3bb2d0"))),
                PropertyFactory.lineWidth(3f)
            )
        }

        try {
            style.addLayer(lineLayer)
        } catch (e: IllegalStateException) {
            logi("layer")
        } catch (e: CannotAddLayerException) {
            loge("anjay ada layer")
        }
    }
}