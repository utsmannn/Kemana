package com.utsman.kemana.driver

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.utsman.kemana.base.logi
import com.utsman.kemana.maputil.EventTracking
import com.utsman.kemana.maputil.MarkerUtil

class MapsCallback(private val context: Context,
                   private val currentLatLng: LatLng,
                   private val onReady: () -> Unit) : OnMapReadyCallback {

    private val liveRotate = MutableLiveData<Float>()
    private val markerUtil = MarkerUtil(context, currentLatLng)
    private lateinit var jsonSource: GeoJsonSource
    private lateinit var symbolLayer: SymbolLayer

    override fun onMapReady(mapboxMap: MapboxMap) {
        jsonSource = GeoJsonSource("driver",
            Feature.fromGeometry(Point.fromLngLat(currentLatLng.longitude, currentLatLng.latitude)))

        liveRotate.postValue(0f)
        mapboxMap.setStyle(Style.OUTDOORS) { style ->
            val position = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            symbolLayer = symbolLayer(style, jsonSource)
            style.addLayer(symbolLayer)

            onReady.invoke()
        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        markerUtil.moveMarkerAnimation(eventTracking.latLngUpdater.newLatLng, jsonSource, symbolLayer, liveRotate)
    }

    private fun symbolLayer(style: Style, jsonSource: GeoJsonSource): SymbolLayer {
        style.addImage("marker", markerUtil.markerVector(R.drawable.ic_marker_driver))
        style.addSource(jsonSource)

        val symbolLayer = SymbolLayer("layer-1", "driver")

        liveRotate.observe(context as LifecycleOwner, Observer { rotation ->
            logi("anjay heading callback livedata --> $rotation")

            symbolLayer.withProperties(
                PropertyFactory.iconImage("marker"),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconRotate(rotation),
                PropertyFactory.iconAllowOverlap(true)
            )
        })
        return symbolLayer
    }
}