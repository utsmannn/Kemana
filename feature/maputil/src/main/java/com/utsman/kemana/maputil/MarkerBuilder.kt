package com.utsman.kemana.maputil

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MarkerBuilder(private val activity: FragmentActivity, private val style: Style) {

    fun newMarker(id: String, latLng: LatLng, @DrawableRes iconVector: Int, vector: Boolean = false): SymbolLayer {
        val markerUtil = MarkerUtil(activity, latLng)

        val jsonSource = GeoJsonSource(
            "source-$id",
            Feature.fromGeometry(Point.fromLngLat(latLng.longitude, latLng.latitude))
        )

        if (vector) {
            style.addImage("marker-$id", markerUtil.markerVector(iconVector))
        } else {
            val markerBitmap =
                BitmapFactory.decodeResource(activity.resources, iconVector)
            style.addImage("marker-$id", markerBitmap)
        }


        style.addSource(jsonSource)

        val symbolLayer = SymbolLayer("layer-$id", "source-$id")

        symbolLayer.withProperties(
            PropertyFactory.iconImage("marker-$id"),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconAllowOverlap(true)
        )
        return symbolLayer
    }
}

fun Style.addMarker(symbolLayer: SymbolLayer) {
    addLayer(symbolLayer)
}