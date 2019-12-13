package com.utsman.kemana.driver.impl.view

import com.mapbox.mapboxsdk.geometry.LatLng

interface ILocationView {
    fun onLocationReady(latLng: LatLng)
    fun getNowLocation()
}