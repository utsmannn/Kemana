package com.utsman.kemana.driver.impl

import android.content.Context
import com.mapbox.mapboxsdk.geometry.LatLng

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView)
    fun getNowLocation(): LatLng
    fun onDestroy()
}