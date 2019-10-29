package com.utsman.kemana.callback

import com.mapbox.mapboxsdk.geometry.LatLng

interface MapReady {
    fun onMapReady(latLng: LatLng)
}