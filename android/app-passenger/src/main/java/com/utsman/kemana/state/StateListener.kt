package com.utsman.kemana.state

import com.utsman.feature.remote.model.Direction

interface StateListener {
    fun doOnNormal()
    fun doOnReady(direction: Direction)
    fun doOnOrder()
}