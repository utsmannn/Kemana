package com.utsman.kemana.maputil

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds

fun LatLng.toLocation(): Location {
    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude

    return location
}

fun calculateBound(center: LatLng, latDistanceInKm: Float, lngDistanceInKm: Float): LatLngBounds {
    val latDistanceInMeters = latDistanceInKm * 1000
    val lngDistanceInMeters = lngDistanceInKm * 1000
    var latDistanceInMeter = latDistanceInMeters
    var lngDistanceInMeter = lngDistanceInMeters
    latDistanceInMeter /= 2f
    lngDistanceInMeter /= 2f


    val ASSUMED_INIT_LATLNG_DIFF = 1.0
    val ACCURACY = 0.01f

    val builder = LatLngBounds.Builder()
    val distance = FloatArray(1)
    run {
        var foundMax = false
        var foundMinLngDiff = 0.0
        var assumedLngDiff = ASSUMED_INIT_LATLNG_DIFF
        do {
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                center.latitude,
                center.longitude + assumedLngDiff,
                distance
            )
            val distanceDiff = distance[0] - lngDistanceInMeters
            if (distanceDiff < 0) {
                if (!foundMax) {
                    foundMinLngDiff = assumedLngDiff
                    assumedLngDiff *= 2.0
                } else {
                    val tmp = assumedLngDiff
                    assumedLngDiff += (assumedLngDiff - foundMinLngDiff) / 2
                    foundMinLngDiff = tmp
                }
            } else {
                assumedLngDiff -= (assumedLngDiff - foundMinLngDiff) / 2
                foundMax = true
            }
        } while (Math.abs(distance[0] - lngDistanceInMeters) > lngDistanceInMeters * ACCURACY)
        val east = LatLng(center.latitude, center.longitude + assumedLngDiff)
        builder.include(east)
        val west = LatLng(center.latitude, center.longitude - assumedLngDiff)
        builder.include(west)
    }
    run {
        var foundMax = false
        var foundMinLatDiff = 0.0
        var assumedLatDiffNorth = ASSUMED_INIT_LATLNG_DIFF
        do {
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                center.latitude + assumedLatDiffNorth,
                center.longitude,
                distance
            )
            val distanceDiff = distance[0] - latDistanceInMeters
            if (distanceDiff < 0) {
                if (!foundMax) {
                    foundMinLatDiff = assumedLatDiffNorth
                    assumedLatDiffNorth *= 2.0
                } else {
                    val tmp = assumedLatDiffNorth
                    assumedLatDiffNorth += (assumedLatDiffNorth - foundMinLatDiff) / 2
                    foundMinLatDiff = tmp
                }
            } else {
                assumedLatDiffNorth -= (assumedLatDiffNorth - foundMinLatDiff) / 2
                foundMax = true
            }
        } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY)
        val north = LatLng(center.latitude + assumedLatDiffNorth, center.longitude)
        builder.include(north)
    }
    run {
        var foundMax = false
        var foundMinLatDiff = 0.0
        var assumedLatDiffSouth = ASSUMED_INIT_LATLNG_DIFF
        do {
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                center.latitude - assumedLatDiffSouth,
                center.longitude,
                distance
            )
            val distanceDiff = distance[0] - latDistanceInMeters
            if (distanceDiff < 0) {
                if (!foundMax) {
                    foundMinLatDiff = assumedLatDiffSouth
                    assumedLatDiffSouth *= 2.0
                } else {
                    val tmp = assumedLatDiffSouth
                    assumedLatDiffSouth += (assumedLatDiffSouth - foundMinLatDiff) / 2
                    foundMinLatDiff = tmp
                }
            } else {
                assumedLatDiffSouth -= (assumedLatDiffSouth - foundMinLatDiff) / 2
                foundMax = true
            }
        } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY)
        val south = LatLng(center.latitude - assumedLatDiffSouth, center.longitude)
        builder.include(south)
    }
    return builder.build()
}