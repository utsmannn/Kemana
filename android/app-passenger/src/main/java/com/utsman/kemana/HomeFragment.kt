package com.utsman.kemana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.feature.base.RxFragment
import com.utsman.kemana.impl.BaseRenderMapsView
import com.utsman.kemana.maps_render.NormalMaps
import com.utsman.kemana.maps_render.OrderMaps
import com.utsman.kemana.maps_render.ReadyMaps
import com.utsman.kemana.state.MainStateMapsView
import com.utsman.kemana.state.StateMapsViewPresenter
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : RxFragment(), MainStateMapsView {

    private val stateMapsView by lazy {
        StateMapsViewPresenter(this)
    }

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mapView = v.map_view

        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.OUTDOORS) { style ->
                setupContent(mapboxMap, style)
            }
        }
        return v
    }

    private fun setupContent(mapboxMap: MapboxMap, style: Style) {
        stateMapsView.renderMapsNormal(mapboxMap, style)

        // another setup
    }

    override fun mapsNormal(mapboxMap: MapboxMap, style: Style): BaseRenderMapsView {
        val maps = NormalMaps(activity!!)
        maps.render(mapboxMap, style)
        return maps
    }

    override fun mapsReady(mapboxMap: MapboxMap, style: Style): BaseRenderMapsView {
        val maps = ReadyMaps(activity!!)
        maps.render(mapboxMap, style)
        return maps
    }

    override fun mapsOrder(mapboxMap: MapboxMap, style: Style): BaseRenderMapsView {
        val maps = OrderMaps(activity!!)
        maps.render(mapboxMap, style)
        return maps
    }


}