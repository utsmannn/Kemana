package com.utsman.kemana.driver.impl.presenter

import com.utsman.kemana.remote.place.Places

interface OrderInterface {
    fun onPickup(places: Places?)
    fun onTake(places: Places?)
    fun onArrive(places: Places?)
}