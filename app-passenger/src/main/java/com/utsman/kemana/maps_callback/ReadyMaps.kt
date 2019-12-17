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

package com.utsman.kemana.maps_callback

import android.content.Context
import android.graphics.Color
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.camera.CameraPosition
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
import com.utsman.kemana.R
import com.utsman.kemana.base.BaseDisposableCompletable
import com.utsman.kemana.base.dp
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker

class ReadyMaps(
    private val context: Context?,
    private val startLatLng: LatLng,
    private val destinationLatLng: LatLng,
    private val polyString: String?,
    private val layer: (map: MapboxMap) -> Unit
) : OnMapReadyCallback, BaseDisposableCompletable() {

    private lateinit var mapbox: MapboxMap
    private lateinit var style: Style

    override fun onMapReady(mapbox: MapboxMap) {
        this.mapbox = mapbox

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            this.style = style
            val markerOptionStart = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLng)
                .setId("start", true)
                .build(context!!)

            val markerOptionDestination = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(destinationLatLng)
                .setId("end", true)
                .build(context)

            if (polyString != null) {
                setupPolylineRoute(polyString, style) {
                    layer.invoke(mapbox)

                    val markerStart = mapbox.addMarker(markerOptionStart)
                    val markerDestination = mapbox.addMarker(markerOptionDestination)
                }

            }
        }
    }

    private fun setupPolylineRoute(geometry: String, style: Style, ok: () -> Unit) {
        val id = "source-route"

        val lineString = LineString.fromPolyline(geometry, 5)
        val featureRoute = Feature.fromGeometry(lineString)
        val sourceRoute = GeoJsonSource(id, featureRoute)

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

        ok.invoke()
    }

    fun setPaddingBottom(padding: Int) {
        mapbox.uiSettings.setLogoMargins(30, 30, 30,(context!!.dp(padding)) + 30)

        val latLngBounds = LatLngBounds.Builder()
            .include(startLatLng)
            .include(destinationLatLng)
            .build()

        mapbox.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                200, 200, 200, (context.dp(padding)) + 200
            )
        )
    }

    override fun onComplete() {
        super.onComplete()
        style.layers.clear()
    }
}