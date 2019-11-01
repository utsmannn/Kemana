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
import com.utsman.kemana.maputil.MarkerUtil

class MapsCallback(private val context: Context,
                   private val currentLatLng: LatLng,
                   private val onReady: () -> Unit) : OnMapReadyCallback {

    private val markerUtil = MarkerUtil(context)
    private lateinit var style: Style
    private lateinit var marker: MarkerUtil.Marker

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.OUTDOORS) { style ->
            this.style = style

            val position = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            marker = markerUtil.addMarker("driver", style, R.drawable.ic_marker_driver, true, currentLatLng)

            onReady.invoke()
        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        marker.moveMarkerAnimation(eventTracking.latLngUpdater.newLatLng)
    }
}