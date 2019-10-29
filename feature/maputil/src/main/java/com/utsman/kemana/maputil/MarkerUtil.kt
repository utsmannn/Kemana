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

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils

class MarkerUtil(private val context: Context, private var currentLatLng: LatLng) {

    private var animator: ValueAnimator? = null

    fun moveMarkerAnimation(newLatLng: LatLng, jsonSource: GeoJsonSource, symbolLayer: SymbolLayer, liveRotate: MutableLiveData<Float>): Boolean {
        if (animator != null && animator!!.isStarted) {
            currentLatLng = animator!!.animatedValue as LatLng
            animator!!.cancel()
        }

        animator = ObjectAnimator.ofObject(latLngEvaluator, currentLatLng, newLatLng).apply {
            duration = 2000
            addUpdateListener(animatorUpdateListener(jsonSource))
        }

        animator?.start()
        rotateMarker(symbolLayer, getAngle(currentLatLng, newLatLng).toFloat(), liveRotate)
        return true
    }

    fun markerVector(@DrawableRes marker: Int): Bitmap {
        val drawable = ResourcesCompat.getDrawable(context.resources,
            marker, null)
        return BitmapUtils.getBitmapFromDrawable(drawable) ?: BitmapFactory.decodeResource(context.resources,
            R.drawable.mapbox_marker_icon_default
        )
    }

    private fun animatorUpdateListener(jsonSource: GeoJsonSource) : ValueAnimator.AnimatorUpdateListener {
        return ValueAnimator.AnimatorUpdateListener { value ->
            val animatedLatLng = value.animatedValue as LatLng
            jsonSource.setGeoJson(Point.fromLngLat(animatedLatLng.longitude, animatedLatLng.latitude))
        }
    }

    fun getAngle(fromLatLng: LatLng, toLatLng: LatLng) : Double {
        var heading = 0.0
        if (fromLatLng != toLatLng) {
            heading = MathUtil.computeHeading(fromLatLng, toLatLng)
        }

        return heading
    }

    private fun rotateMarker(symbolLayer: SymbolLayer, toRotation: Float, liveRotate: MutableLiveData<Float>) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val startRotation = symbolLayer.iconRotate.value
        val duration: Long = 200

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = LinearInterpolator().getInterpolation(elapsed.toFloat() / duration)

                val rot = t * toRotation + (1 - t) * startRotation

                val rotation = if (-rot > 180) rot / 2 else rot
                liveRotate.postValue(rotation)
                if (t < 1.0) {
                    handler.postDelayed(this, 100)
                }
            }
        })
    }
}