package com.utsman.kemana.presenter

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.impl.view.IMapView
import com.utsman.kemana.impl.presenter.MapsInterface
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

class MapsPresenter(private val iMapView: IMapView) :
    MapsInterface {
    override fun mapStart(startLatLng: LatLng) {
        iMapView.mapStart(startLatLng)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        iMapView.mapReady(start, destination, polyline)
    }

    override fun mapOrder() {
        iMapView.mapOrder()
    }

    override fun failedServerConnection() {
        iMapView.failedServerConnection()
    }
}