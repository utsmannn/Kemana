package com.utsman.kemana.driver.impl.presenter

import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import com.utsman.kemana.remote.driver.OrderData

interface MapsInterface {
    fun initMap(locationSubs: LocationSubs)
    fun startUpdate(updateLocationSubs: UpdateLocationSubs)
    fun pickupPassenger(orderData: OrderData)
}