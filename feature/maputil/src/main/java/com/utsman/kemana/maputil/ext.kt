/*
 * Copyright 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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