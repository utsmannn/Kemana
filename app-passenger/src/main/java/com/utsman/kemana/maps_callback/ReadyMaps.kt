package com.utsman.kemana.maps_callback

import android.content.Context
import android.graphics.Color
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
import com.utsman.kemana.R
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
) : OnMapReadyCallback {
    override fun onMapReady(mapbox: MapboxMap) {

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            val markerOptionStart = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLng)
                .setId("start")
                .build(context!!)

            val markerOptionDestination = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(destinationLatLng)
                .setId("end")
                .build(context)

            if (polyString != null) {
                setupPolylineRoute(polyString, style) {
                    layer.invoke(mapbox)

                    val markerStart = mapbox.addMarker(markerOptionStart).get("start")
                    val markerDestination = mapbox.addMarker(markerOptionDestination).get("end")
                }

            }
            val latLngBounds = LatLngBounds.Builder()
                .include(startLatLng)
                .include(destinationLatLng)
                .build()

            mapbox.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds,
                    200, 200, 200, 200
                )
            )
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
}