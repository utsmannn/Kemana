package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView)
    fun getNowLocation(): LatLng
    fun onDestroy()
}