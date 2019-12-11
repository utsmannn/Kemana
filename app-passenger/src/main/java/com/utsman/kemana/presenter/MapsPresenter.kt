package com.utsman.kemana.presenter

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.impl.IMapView
import com.utsman.kemana.impl.MapsInterface
import com.utsman.kemana.remote.place.Places

class MapsPresenter(private val iMapView: IMapView) : MapsInterface {
    override fun mapStart(startLatLng: LatLng) {
        iMapView.mapStart(startLatLng)
    }

    override fun mapReady(start: Places, destination: Places) {
        iMapView.mapReady(start, destination)
    }

    override fun mapOrder() {
        iMapView.mapOrder()
    }


}