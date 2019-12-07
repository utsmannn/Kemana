package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationView {
    fun onLocationReady(latLng: LatLng)
    fun getNowLocation()
}