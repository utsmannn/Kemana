package com.utsman.kemana.impl.presenter

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.impl.view.ILocationView

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun getNowLocation(): LatLng
    fun onDestroy()
}