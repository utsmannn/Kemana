package com.utsman.kemana.impl.presenter

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

interface MapsInterface {
    fun mapStart(startLatLng: LatLng)
    fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?)
    fun mapOrder(orderData: OrderData)
    fun failedServerConnection()
    fun dispose()
}