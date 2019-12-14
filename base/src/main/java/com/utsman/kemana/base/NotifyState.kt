package com.utsman.kemana.base

data class NotifyState(val state: Int) {

    companion object {
        const val UPDATE_LOCATION = 0
        const val STOP_UPDATE_LOCATION = 1
        const val READY = 2

        const val DRIVER_READY = 1100
        const val DRIVER_UNREADY = 1101
    }
}