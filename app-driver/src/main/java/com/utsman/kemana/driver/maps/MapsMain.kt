package com.utsman.kemana.driver.maps

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.EventTracking
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker

class MapsMain(private val context: Context,
               private val currentLatLng: LatLng,
               private val onReady: () -> Unit) : OnMapReadyCallback {

    private lateinit var style: Style
    private var marker: Marker? = null

    override fun onMapReady(mapboxMap: MapboxMap) {
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

            onReady.invoke()
        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        marker?.moveMarkerSmoothly(eventTracking.latLngUpdater.newLatLng)
    }
}