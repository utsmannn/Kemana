package com.utsman.kemana.maps_render

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.R
import com.utsman.kemana.impl.BaseRenderMapsView
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import com.utsman.smartmarker.mapbox.toLatLngMapbox

class NormalMaps(private val context: Context) : BaseRenderMapsView {
    private val locationWatcher = LocationWatcher(context)
    private var meMarker: Marker? = null

    override fun render(mapboxMap: MapboxMap, style: Style) {
        locationWatcher.getLocation {
            val meMarkerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.ic_pin_people, true)
                .setPosition(it.toLatLngMapbox())
                .setId("me", true)
                .build(context)

            meMarker = mapboxMap.addMarker(meMarkerOption)
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLngMapbox(), 16.0))
        }
    }

    override fun remove(style: Style) {
        meMarker?.getId()?.let { id ->
            style.removeLayer(id)
        }
    }
}