package com.utsman.kemana.backend

import com.utsman.kemana.backend.model.Position
import kotlin.math.*

// calculate distance from location to another location
fun Position.distanceTo(position: Position): Double {
    val lat1 = lat ?: 0.0
    val lon1 = lon ?: 0.0
    val lat2 = position.lat ?: 0.0
    val lon2 = position.lon ?: 0.0

    val R = 6371

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = sin(latDistance / 2) * sin(latDistance / 2) + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R.toDouble() * c * 1000.0 // convert to meters

    distance = distance.pow(2.0) + 0.0.pow(2.0)

    return sqrt(distance)

}