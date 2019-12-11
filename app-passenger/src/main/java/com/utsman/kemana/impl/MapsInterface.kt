package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

interface MapsInterface {
    fun mapStart(startLatLng: LatLng)
    fun mapReady(start: Places, destination: Places, polyline: PolylineResponses)
    fun mapOrder()
}