package com.utsman.kemana

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.places.Geometry

fun Geometry.toLatLng(): LatLng {
    return LatLng(coordinates[1], coordinates[0])
}