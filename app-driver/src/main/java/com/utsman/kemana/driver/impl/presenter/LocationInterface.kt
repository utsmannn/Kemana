package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.driver.impl.view.ILocationUpdateView
import com.utsman.kemana.driver.impl.view.ILocationView

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView)
    fun getNowLocation(): LatLng
    fun onDestroy()
}