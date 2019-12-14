package com.utsman.kemana.driver.impl.presenter

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.driver.impl.view.ILocationUpdateView
import com.utsman.kemana.driver.impl.view.ILocationView
import io.reactivex.disposables.Disposable

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView): Disposable
    fun getNowLocation(): LatLng
    fun onDestroy()
}