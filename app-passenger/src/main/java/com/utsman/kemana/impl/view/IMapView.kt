package com.utsman.kemana.impl.view

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

interface IMapView {
    fun mapStart(startLatLng: LatLng)
    fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?)
    fun mapPickup(orderData: OrderData)
    fun failedServerConnection()
    fun dispose()
}