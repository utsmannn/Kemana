package com.utsman.kemana.driver.impl.presenter

import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs

interface MapsInterface {
    fun initMap(locationSubs: LocationSubs)
    fun startUpdate(updateLocationSubs: UpdateLocationSubs)
    fun pickupPassenger()
}