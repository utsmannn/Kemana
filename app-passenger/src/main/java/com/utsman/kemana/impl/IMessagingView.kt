package com.utsman.kemana.impl

import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

interface IMessagingView {
    fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses)
    fun retrieveDriver()
}