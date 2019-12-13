package com.kemana.backend

import com.kemana.backend.model.Position

fun distance(lat1: Double, lat2: Double, lon1: Double,
             lon2: Double): Double {
    val R = 6371 // Radius of the earth

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R.toDouble() * c * 1000.0 // convert to meters

    distance = Math.pow(distance, 2.0) + Math.pow(0.0, 2.0)

    return Math.sqrt(distance)
}

fun Position.distanceTo(position: Position): Double {
    val lat1 = lat!!
    val lon1 = lon!!
    val lat2 = position.lat!!
    val lon2 = position.lon!!

    val R = 6371

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R.toDouble() * c * 1000.0 // convert to meters

    distance = Math.pow(distance, 2.0) + Math.pow(0.0, 2.0)

    return Math.sqrt(distance)

}