package com.utsman.kemana.driver.impl.view

import com.utsman.kemana.remote.place.Places

interface IOrderView {
    fun onPickup(places: Places?)
    fun onTake(places: Places?)
    fun onArrive(places: Places?)
}