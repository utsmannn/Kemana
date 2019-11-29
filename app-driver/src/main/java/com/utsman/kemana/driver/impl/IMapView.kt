package com.utsman.kemana.driver.impl

import android.app.Activity
import com.mapbox.mapboxsdk.geometry.LatLng

interface IMapView {
    fun onLocationReady(latLng: LatLng)
    fun onLocationUpdate(newLatLng: LatLng)
}