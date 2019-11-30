package com.utsman.kemana.base

data class NotifyState(val state: Int) {

    companion object {
        const val UPDATE_LOCATION = 0
        const val UPDATE_CAMERA = 1
        const val READY = 2
    }
}