package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationUpdateView {
    fun onLocationUpdate(newLatLng: LatLng)
}