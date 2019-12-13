package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationUpdateView {
    fun onLocationUpdateOld(oldLatLng: LatLng)
    fun onLocationUpdate(newLatLng: LatLng)
}