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

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.base.ext.addTimer
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.EventTracking
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import io.reactivex.disposables.CompositeDisposable

class MapsMain(private val context: Context,
               private val disposable: CompositeDisposable,
               private val locationWatcher: LocationWatcher,
               private val currentLatLng: LatLng,
               private val onReady: (MapboxMap?) -> Unit) : OnMapReadyCallback {

    private lateinit var style: Style
    private var marker: Marker? = null
    private var map: MapboxMap? = null

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        mapboxMap.setStyle(Style.OUTDOORS) { style ->
            this.style = style

            val position = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            val markerOption = MarkerOptions.Builder()
                .setId("driver")
                .addIcon(R.drawable.ic_marker_driver, true)
                .addPosition(currentLatLng)
                .build(context)

            val markerLayer = mapboxMap.addMarker(markerOption)
            marker = markerLayer.get("driver")

            onReady.invoke(mapboxMap)

            /*disposable.addTimer(5) {
                locationWatcher.getLocation {
                    val newPosition = CameraPosition.Builder()
                        .target(it.toLatLngMapbox())
                        .build()

                    val cameraPosition = CameraUpdateFactory.newCameraPosition(newPosition)
                    map?.animateCamera(cameraPosition)
                }
            }*/

            val locationComponentOption = LocationComponentOptions.builder(context)
                .bearingTintColor(R.color.colorPrimary)
                .accuracyAlpha(0.8f) // 0.0f - 1.0f
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(context, style)
                .locationComponentOptions(locationComponentOption)
                .useDefaultLocationEngine(true)
                .build()

            val locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(locationComponentActivationOptions)

            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING_GPS
            locationComponent.renderMode = RenderMode.NORMAL
        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        marker?.moveMarkerSmoothly(eventTracking.latLngUpdater.newLatLng)
    }
}