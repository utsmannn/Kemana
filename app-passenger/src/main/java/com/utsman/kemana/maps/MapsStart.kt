package com.utsman.kemana.maps

import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.R
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarkers
import io.reactivex.disposables.CompositeDisposable

class MapsStart(private val activity: FragmentActivity, private val disposable: CompositeDisposable, private val onReady: (LatLng) -> Unit) : OnMapReadyCallback {
    private var currentLatLng: LatLng = LatLng()
    private var paddingBottom = 0

    fun setCurrentLatLng(currentLatLng: LatLng) {
        this.currentLatLng = currentLatLng
    }

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        val backendlessApp = BackendlessApp(activity.application, disposable)
        mapboxMap.setStyle(Style.OUTDOORS) { style ->
            val position = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(17.0)
                .padding(0.0, 0.0, 0.0,  paddingBottom.toDouble())
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
            mapboxMap.uiSettings.setLogoMargins(30, 30, 30,paddingBottom + 30)


            val markerOption = MarkerOptions.Builder()
                .setId("device")
                .addIcon(R.drawable.mapbox_marker_icon_default, true)
                .addPosition(currentLatLng)
                .build(activity)

            val markerLayer = mapboxMap.addMarkers(markerOption)

            onReady.invoke(currentLatLng)

            /*val marker = MarkerUtil(activity, currentLatLng)
            marker.addMarker("device", style, R.drawable.mapbox_marker_icon_default, false, latLng = {
                return@addMarker currentLatLng
            })


            backendlessApp.getDriversList().observe(activity as LifecycleOwner, Observer { users ->
                onReady.invoke(currentLatLng)
                mapboxMap.uiSettings.setLogoMargins(30,30,30, paddingBottom + 30)
                for (user in users) {
                    if (user.lat != null && user.lon != null) {
                        val latLng = LatLng(user.lat!!, user.lon!!)

                        val driverMarker = MarkerUtil(activity, latLng)
                        driverMarker.addMarker("driver", style, R.drawable.ic_marker_driver, true, symbolLayer = {
                            it.withProperties(PropertyFactory.iconRotate(user.angle!!.toFloat()))
                        }) {
                            return@addMarker latLng
                        }

                        *//*val driverMarker =
                            markerBuilder.newMarker(
                                "${user.lat}-${user.lon}",
                                latLng,
                                R.drawable.ic_marker_driver,
                                true
                            )
                        driverMarker.withProperties(PropertyFactory.iconRotate(user.angle!!.toFloat()))
                        style.addMarker(driverMarker)*//*
                    }
                }
            })*/
        }
    }
}