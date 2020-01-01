package com.utsman.kemana.driver.maps_render

import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.feature.base.replaceFragment
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.control.NormalControlFragment
import com.utsman.kemana.driver.impl.BaseRenderMapsView
import com.utsman.kemana.driver.state.StateListener
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import com.utsman.smartmarker.mapbox.toLatLngMapbox

class NormalMaps(private val activity: FragmentActivity) : BaseRenderMapsView {
    private val locationWatcher = LocationWatcher(activity)
    private var meMarker: Marker? = null

    override fun render(mapboxMap: MapboxMap, style: Style, stateListener: StateListener) {
        locationWatcher.getLocation {
            val meMarkerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.ic_pin_people, true)
                .setPosition(it.toLatLngMapbox())
                .setId("me", true)
                .build(activity)

            meMarker = mapboxMap.addMarker(meMarkerOption)
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLngMapbox(), 16.0))
        }

        val normalControlFragment = NormalControlFragment()

        activity.replaceFragment(normalControlFragment, R.id.control_container)

       /* val normalControlImpl = object : NormalControlImpl {
            override fun onSelectMapsPicker() {

            }

            override fun onFindingOrder(order: Order) {

            }

            override fun toReadyMaps(direction: Direction) {
                stateListener.doOnReady(direction)
            }

        }

        val normalControlFragment = NormalControlFragment(normalControlImpl)

        activity.replaceFragment(normalControlFragment, R.id.control_container)*/

    }

    override fun remove(style: Style) {
        meMarker?.getId()?.let { id ->
            style.removeLayer(id)
        }
    }
}