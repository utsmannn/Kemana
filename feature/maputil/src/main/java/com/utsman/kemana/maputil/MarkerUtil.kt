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
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils

class MarkerUtil(private val context: Context) {

    fun markerVector(@DrawableRes marker: Int): Bitmap {
        val drawable = ResourcesCompat.getDrawable(context.resources,
            marker, null)
        return BitmapUtils.getBitmapFromDrawable(drawable) ?: BitmapFactory.decodeResource(context.resources,
            R.drawable.mapbox_marker_icon_default
        )
    }

    fun addMarker(id: String, style: Style, @DrawableRes icon: Int, vector: Boolean, latLng: LatLng, symbolLayer: ((SymbolLayer) -> Unit)? = null): Marker {
        val markerBuilder = MarkerBuilder(context, style)
        val marker = markerBuilder.newMarker(id, latLng, icon, vector)
        symbolLayer?.invoke(marker)
        style.addMarker(marker)

        val jsonSource = markerBuilder.jsonSource

        return Marker(latLng, jsonSource, marker)
    }

    class Marker(private var currentLatLng: LatLng, private val jsonSource: GeoJsonSource, private val symbolLayer: SymbolLayer) {
        private var animator: ValueAnimator? = null
        private val liveRotate = MutableLiveData<Float>()

        init {
            liveRotate.postValue(0f)
            liveRotate.observeForever {
                symbolLayer.withProperties(PropertyFactory.iconRotate(it))
            }
        }

        fun moveMarkerAnimation(latLng: LatLng): Boolean {
            if (animator != null && animator!!.isStarted) {
                currentLatLng = animator!!.animatedValue as LatLng
                animator!!.cancel()
            }

            animator = ObjectAnimator.ofObject(latLngEvaluator, currentLatLng, latLng).apply {
                duration = 2000
                addUpdateListener(animatorUpdateListener(jsonSource))
            }

            animator?.start()
            rotateMarker(symbolLayer, getAngle(currentLatLng, latLng).toFloat())
            return true
        }

        private fun animatorUpdateListener(jsonSource: GeoJsonSource) : ValueAnimator.AnimatorUpdateListener {
            return ValueAnimator.AnimatorUpdateListener { value ->
                val animatedLatLng = value.animatedValue as LatLng
                jsonSource.setGeoJson(Point.fromLngLat(animatedLatLng.longitude, animatedLatLng.latitude))
            }
        }

        private fun rotateMarker(symbolLayer: SymbolLayer, toRotation: Float) {
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            var startRotation = symbolLayer.iconRotate.value
            val duration: Long = 200

            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = LinearInterpolator().getInterpolation(elapsed.toFloat() / duration)

                    val rot = t * toRotation + (1 - t) * startRotation

                    val rotation = if (-rot > 180) rot / 2 else rot
                    liveRotate.postValue(rotation)
                    startRotation = liveRotate.value ?: 0f
                    if (t < 1.0) {
                        handler.postDelayed(this, 100)
                    }
                }
            })
        }
    }
}

fun getAngle(fromLatLng: LatLng, toLatLng: LatLng) : Double {
    var heading = 0.0
    if (fromLatLng != toLatLng) {
        heading = MathUtil.computeHeading(fromLatLng, toLatLng)
    }

    return heading
}