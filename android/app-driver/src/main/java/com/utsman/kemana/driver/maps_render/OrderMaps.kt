package com.utsman.kemana.driver.maps_render

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.driver.impl.BaseRenderMapsView
import com.utsman.kemana.driver.state.StateListener

class OrderMaps(private val activity: FragmentActivity) : BaseRenderMapsView {

    override fun render(mapboxMap: MapboxMap, style: Style, stateListener: StateListener) {

    }

    override fun remove(style: Style) {

    }
}