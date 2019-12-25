package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

interface BaseRenderMapsView {
    fun render(mapboxMap: MapboxMap, style: Style)
    fun remove(style: Style)
}