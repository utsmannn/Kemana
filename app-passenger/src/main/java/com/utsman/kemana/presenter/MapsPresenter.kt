package com.utsman.kemana.presenter

import com.utsman.kemana.impl.IMapView
import com.utsman.kemana.impl.MapsInterface
import com.utsman.kemana.subscriber.LocationSubs

class MapsPresenter(private val iMapView: IMapView) : MapsInterface {

    override fun initMap(locationSubs: LocationSubs) {
        iMapView.onLocationReady(locationSubs.latLng)
    }
}