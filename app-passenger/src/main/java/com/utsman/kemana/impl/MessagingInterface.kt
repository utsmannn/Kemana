package com.utsman.kemana.impl

import com.utsman.kemana.remote.place.Places

interface MessagingInterface {
    fun findDriver(startPlaces: Places, destPlaces: Places)
    fun retrieveDriver()
}