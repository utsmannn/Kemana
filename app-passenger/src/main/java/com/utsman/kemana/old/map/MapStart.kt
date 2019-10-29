package com.utsman.kemana.old.map

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.utsman.kemana.R
import com.utsman.kemana.auth.User
import com.utsman.kemana.backendless.BackendlessViewModel
import com.utsman.kemana.callback.MapReady
import com.utsman.kemana.maputil.MarkerBuilder
import com.utsman.kemana.maputil.MarkerUtil
import com.utsman.kemana.maputil.addMarker

class MapStart(private val activity: FragmentActivity, private val mapReady: MapReady) : OnMapReadyCallback {

    private var currentLatLng: LatLng = LatLng()
    private val markerUtil = MarkerUtil(activity, currentLatLng)
    private var paddingBottom = 0

    fun setCurrentLatLng(currentLatLng: LatLng) {
        this.currentLatLng = currentLatLng
    }

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        val backendlessViewModel = ViewModelProviders.of(activity)[BackendlessViewModel::class.java]

        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            val position = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17.0)
                .padding(0.0, 0.0, 0.0,  paddingBottom.toDouble())
                .build()

            mapboxMap.uiSettings.setLogoMargins(30,30,30, paddingBottom + 30)
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            val markerBuilder = MarkerBuilder(activity, style)
            val marker = markerBuilder.newMarker("device", currentLatLng, R.drawable.mapbox_marker_icon_default, false)
            style.addMarker(marker)

            backendlessViewModel.getDriversList().observe(activity as LifecycleOwner, Observer { users ->
                for (user in users) {
                    setupMarkerDriver(user, style)
                }
            })

            mapReady.onMapReady(currentLatLng)
        }
    }

    private fun setupMarkerDriver(user: User, style: Style) {
        val jsonSource = GeoJsonSource(user.objectId,
            Feature.fromGeometry(Point.fromLngLat(user.lon!!, user.lat!!)))

        style.addImage("marker-${user.objectId}", markerUtil.markerVector(R.drawable.ic_marker_driver))

        style.addSource(jsonSource)

        val symbolLayer = SymbolLayer("layer-${user.objectId}", user.objectId)
        symbolLayer.withProperties(
            PropertyFactory.iconImage("marker-${user.objectId}"),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconRotate(user.angle!!.toFloat()),
            PropertyFactory.iconAllowOverlap(true))

        style.addLayer(symbolLayer)

    }
}