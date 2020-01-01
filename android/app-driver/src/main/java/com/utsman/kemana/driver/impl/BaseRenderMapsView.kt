package com.utsman.kemana.driver.impl

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.driver.state.StateListener

interface BaseRenderMapsView {
    fun render(mapboxMap: MapboxMap, style: Style, stateListener: StateListener)
    fun remove(style: Style)
}