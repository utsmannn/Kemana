package com.utsman.kemana.impl

import com.utsman.kemana.subscriber.LocationSubs

interface MapsInterface {
    fun initMap(locationSubs: LocationSubs)
}