package com.utsman.kemana.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.feature.base.RxFragment
import com.utsman.feature.base.toast
import com.utsman.feature.remote.model.Direction
import com.utsman.kemana.driver.impl.BaseRenderMapsView
import com.utsman.kemana.driver.maps_render.NormalMaps
import com.utsman.kemana.driver.maps_render.PickupMaps
import com.utsman.kemana.driver.state.MainStateMapsView
import com.utsman.kemana.driver.state.StateListener
import com.utsman.kemana.driver.state.StateMapsViewPresenter
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : RxFragment(), MainStateMapsView {

    private val stateMapsView by lazy {
        StateMapsViewPresenter(this)
    }

    private lateinit var mapView: MapView
    private lateinit var stateListener: StateListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mapView = v.mapview

        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.OUTDOORS) { style ->
                setupContent(mapboxMap, style)
            }
        }

        return v
    }

    private fun setupContent(mapboxMap: MapboxMap, style: Style) {
        stateMapsView.renderMapsNormal(mapboxMap, style)

        stateListener = object : StateListener {
            override fun doOnNormal() {
                stateMapsView.renderMapsNormal(mapboxMap, style)
            }

            override fun doOnPickup(direction: Direction) {
                stateMapsView.renderMapsPickup(mapboxMap, style, direction)
            }

            override fun doOnOrder() {
                stateMapsView.renderMapsOrder(mapboxMap, style)
            }

        }
    }

    override fun mapsNormal(mapboxMap: MapboxMap, style: Style): BaseRenderMapsView {
        val maps = NormalMaps(activity!!)
        maps.render(mapboxMap, style, stateListener)
        return maps
    }

    override fun mapsPickup(mapboxMap: MapboxMap, style: Style, direction: Direction): BaseRenderMapsView {
        val maps = PickupMaps(activity!!, direction)
        maps.render(mapboxMap, style, stateListener)
        return maps
    }

    override fun mapsOrder(mapboxMap: MapboxMap, style: Style): BaseRenderMapsView {
        val maps = NormalMaps(activity!!)
        maps.render(mapboxMap, style, stateListener)
        return maps
    }
}