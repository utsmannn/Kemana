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
    private val startLatLng: LatLng,
    private val layer: (map: MapboxMap, marker: Marker?) -> Unit
) : OnMapReadyCallback {

    override fun onMapReady(mapbox: MapboxMap) {

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            val markerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLng)
                .setId("me")
                .build(context!!)

            val marker = mapbox.addMarker(markerOption).get("me")

            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.0))
            layer.invoke(mapbox, marker)
        }
    }
}