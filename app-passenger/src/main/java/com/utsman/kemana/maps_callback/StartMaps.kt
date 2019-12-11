package com.utsman.kemana.maps_callback

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.R
import com.utsman.kemana.base.logi
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.disposables.CompositeDisposable

class StartMaps(
    private val disposable: CompositeDisposable,
    private val context: Context?,
    private val startLatLng: LatLng,
    private val layer: (map: MapboxMap, marker: Marker?) -> Unit
) : OnMapReadyCallback {

    override fun onMapReady(mapbox: MapboxMap) {

        val remotePresenter =
            RemotePresenter(disposable)

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            val markerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLng)
                .setId("me")
                .build(context!!)

            val marker = mapbox.addMarker(markerOption).get("me")

            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.0))

            remotePresenter.getDriversActive { drivers ->
                logi("driver found --> ${drivers?.size}")
                drivers?.forEachIndexed { index, driver ->
                    if (driver.position != null) {
                        val latLngDriver = LatLng(driver.position!!.lat!!, driver.position!!.lon!!)
                        val rotation = driver.position?.angle

                        logi("driver location --> $latLngDriver, id --> ${driver.id}, rotation -> $rotation from ${driver.position?.angle}")

                        val markerDriverOption = MarkerOptions.Builder()
                            .setId(driver.id!!)
                            .setIcon(R.drawable.mapbox_marker_icon_default, true)
                            .setPosition(latLngDriver)
                            .setRotation(rotation)
                            .build(context)

                        mapbox.addMarker(markerDriverOption).get(driver.id!!)
                    }
                }

                layer.invoke(mapbox, marker)
            }
        }
    }
}