package com.utsman.kemana.driver.impl

import android.content.Context

interface LocationInterface {
    fun initLocation(iLocationView: ILocationView)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView)
    fun onDestroy()
}