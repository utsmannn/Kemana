package com.utsman.kemana.driver.state

import com.utsman.feature.remote.model.Direction

interface StateListener {
    fun doOnNormal()
    fun doOnPickup(direction: Direction)
    fun doOnOrder()
}