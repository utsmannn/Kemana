package com.utsman.kemana.maps_render

import android.graphics.Color
import androidx.fragment.app.FragmentActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CannotAddLayerException
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.CannotAddSourceException
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils
import com.utsman.feature.remote.model.Direction
import com.utsman.kemana.R
import com.utsman.kemana.impl.BaseRenderMapsView
import com.utsman.kemana.state.StateListener
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker

class ReadyMaps(private val activity: FragmentActivity, private val direction: Direction) : BaseRenderMapsView {

    private val fromLatLon = LatLng(direction.fromCoordinate[0], direction.fromCoordinate[1])
    private val toLatLon = LatLng(direction.toCoordinate[0], direction.toCoordinate[1])

    private val markerFromOption = MarkerOptions.Builder()
        .setId("from", true)
        .setPosition(fromLatLon)
        .setIcon(R.drawable.mapbox_marker_icon_default, false)
        .build(activity)

    private val markerToOption = MarkerOptions.Builder()
        .setId("to", true)
        .setPosition(toLatLon)
        .setIcon(R.drawable.mapbox_marker_icon_default, false)
        .build(activity)

    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null

    private val latLngBounds = LatLngBounds.Builder()
        .include(fromLatLon)
        .include(toLatLon)
        .build()

    private val sourceId = "source-poly"
    private val lineId = "layer-poly"

    private var sourceRoute: GeoJsonSource? = null
    private var lineLayer: LineLayer? = null

    override fun render(mapboxMap: MapboxMap, style: Style, stateListener: StateListener) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200, 200, 200, 200))

        setupPolylineRoute(direction.geometry, style) {
            markerFrom = mapboxMap.addMarker(markerFromOption)
            markerTo = mapboxMap.addMarker(markerToOption)
        }
    }

    private fun setupPolylineRoute(geometry: String, style: Style, ok: () -> Unit) {

        val lineString = LineString.fromPolyline(geometry, 5)
        val featureRoute = Feature.fromGeometry(lineString)
        sourceRoute = GeoJsonSource("$sourceId-${System.currentTimeMillis()}", featureRoute)

        try {
            style.addSource(sourceRoute!!)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: CannotAddSourceException) {
            e.printStackTrace()
        }

        lineLayer = LineLayer("$lineId-${System.currentTimeMillis()}", sourceRoute!!.id).apply {
            withProperties(
                PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.parseColor("#3bb2d0"))),
                PropertyFactory.lineWidth(3f)
            )
        }

        try {
            style.addLayer(lineLayer!!)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: CannotAddLayerException) {
            e.printStackTrace()
        }

        ok.invoke()
    }

    override fun remove(style: Style) {

        sourceRoute?.id?.let { id ->
            style.removeSource(id)
        }

        lineLayer?.id?.let { id ->
            style.removeLayer(id)
        }

        markerFrom?.getId()?.let { id ->
            style.removeLayer(id)
        }

        markerTo?.getId()?.let { id ->
            style.removeLayer(id)
        }
    }
}