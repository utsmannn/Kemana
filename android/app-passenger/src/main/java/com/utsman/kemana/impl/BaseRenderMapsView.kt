package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.state.StateListener

interface BaseRenderMapsView {
    fun render(mapboxMap: MapboxMap, style: Style, stateListener: StateListener)
    fun remove(style: Style)
}