package com.utsman.kemana.driver.impl

import android.content.Context

interface LocationInterface {
    fun initLocation(context: Context)
    fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView)
    fun onDestroy()
}