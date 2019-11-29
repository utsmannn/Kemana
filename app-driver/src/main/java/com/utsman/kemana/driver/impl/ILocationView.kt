package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationView {
    fun onLocationReady(latLng: LatLng)
}