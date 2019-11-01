package com.utsman.kemana.maputil

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MarkerBuilder(private val context: Context, private val style: Style) {

    internal lateinit var jsonSource: GeoJsonSource

    private fun newSymbol(id: String): SymbolLayer {
        return SymbolLayer("layer-$id", "source-$id")
    }

    internal fun newMarker(id: String, latLng: LatLng, @DrawableRes iconVector: Int, vector: Boolean = false): SymbolLayer {
        val symbolLayer = newSymbol(id)
        val markerUtil = MarkerUtil(context)

        jsonSource = GeoJsonSource(
            "source-$id",
            Feature.fromGeometry(Point.fromLngLat(latLng.longitude, latLng.latitude))
        )

        if (vector) {
            style.addImage("marker-$id", markerUtil.markerVector(iconVector))
        } else {
            val markerBitmap =
                BitmapFactory.decodeResource(context.resources, iconVector)
            style.addImage("marker-$id", markerBitmap)
        }


        style.addSource(jsonSource)

        symbolLayer.withProperties(
            PropertyFactory.iconImage("marker-$id"),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconAllowOverlap(true)
        )
        return symbolLayer
    }
}

internal fun Style.addMarker(symbolLayer: SymbolLayer) {
    addLayer(symbolLayer)
}