package com.utsman.kemana.driver.maps_callback

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style

class MapsPickup : OnMapReadyCallback {
    override fun onMapReady(mapbox: MapboxMap) {
        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->

        }
    }
}