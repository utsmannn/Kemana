package com.utsman.kemana.driver.presenter

import com.utsman.kemana.driver.impl.IMapView
import com.utsman.kemana.driver.impl.MapsInterface
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs

class MapsPresenter(private val iMapView: IMapView) : MapsInterface {

    override fun initMap(locationSubs: LocationSubs) {
        iMapView.onLocationReady(locationSubs.latLng)
    }

    override fun startUpdate(updateLocationSubs: UpdateLocationSubs) {
        iMapView.onLocationUpdate(updateLocationSubs.newLatLng)
    }

    override fun pickupPassenger() {
        iMapView.onPickupPassenger()
    }
}