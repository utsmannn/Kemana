package com.utsman.kemana.impl.presenter

import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

interface MessagingInterface {
    fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses)
    fun orderCancel()
}