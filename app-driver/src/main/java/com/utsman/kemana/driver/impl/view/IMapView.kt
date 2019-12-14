package com.utsman.kemana.driver.impl.view

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.remote.driver.OrderData

interface IMapView {
    fun onLocationReady(latLng: LatLng)
    fun onLocationUpdate(newLatLng: LatLng)
    fun onPickupPassenger(orderData: OrderData)
}