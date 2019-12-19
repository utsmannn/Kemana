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

package com.utsman.kemana.driver.maps_callback

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.driver.R
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker

class MainMaps(
    private val context: Context?,
    private val startLatLon: LatLng,
    private val layer: (map: MapboxMap, marker: Marker?) -> Unit
) : OnMapReadyCallback {

    override fun onMapReady(mapbox: MapboxMap) {

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->

            val markerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLon)
                .setId("me", true)
                .build(context!!)

            val marker = mapbox.addMarker(markerOption)

            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLon, 17.0))
            layer.invoke(mapbox, marker)
        }
    }
}