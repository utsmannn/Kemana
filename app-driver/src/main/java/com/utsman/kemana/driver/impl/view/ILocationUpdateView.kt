package com.utsman.kemana.driver.impl.view

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationUpdateView {
    fun onLocationUpdateOld(oldLatLng: LatLng)
    fun onLocationUpdate(newLatLng: LatLng)
}