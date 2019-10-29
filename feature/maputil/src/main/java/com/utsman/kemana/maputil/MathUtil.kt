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

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MathUtil {
    fun computeHeading(from: LatLng, to: LatLng): Double {
        val fromLat = Math.toRadians(from.latitude)
        val fromLng = Math.toRadians(from.longitude)
        val toLat = Math.toRadians(to.latitude)
        val toLng = Math.toRadians(to.longitude)
        val dLng = toLng - fromLng
        val heading = atan2(
            sin(dLng) * cos(toLat),
            cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng)
        )
        return wrap(Math.toDegrees(heading), -180.0, 180.0)
    }

    private fun wrap(n: Double, min: Double, max: Double): Double {
        return if (n >= min && n < max) n else mod(
            n - min,
            max - min
        ) + min
    }

    private fun mod(x: Double, m: Double): Double {
        return (x % m + m) % m
    }
}