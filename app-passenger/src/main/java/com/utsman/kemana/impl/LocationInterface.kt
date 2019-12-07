package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun getNowLocation(): LatLng
    fun onDestroy()
}