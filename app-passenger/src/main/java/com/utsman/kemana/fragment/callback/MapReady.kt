package com.utsman.kemana.fragment.callback

import com.mapbox.mapboxsdk.geometry.LatLng

interface MapReady {
    fun onMapReady(latLng: LatLng)
}