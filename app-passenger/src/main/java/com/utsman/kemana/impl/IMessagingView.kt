package com.utsman.kemana.impl

import com.utsman.kemana.remote.place.Places

interface IMessagingView {
    fun findDriver(startPlaces: Places, destPlaces: Places)
    fun retrieveDriver()
}