package com.utsman.kemana.maps_callback

import android.content.Context
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.R
import com.utsman.kemana.base.BaseDisposableCompletable
import com.utsman.kemana.base.dp
import com.utsman.kemana.base.logi
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.subscriber.PaddingMapsSubs
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableCompletableObserver
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider

class StartMaps(
    private val disposable: CompositeDisposable,
    private val context: Context?,
    private val startLatLng: LatLng,
    private val layer: (map: MapboxMap, marker: Marker?) -> Unit
) : OnMapReadyCallback, BaseDisposableCompletable() {

    private lateinit var mapbox: MapboxMap
    private lateinit var style: Style

    override fun onMapReady(mapbox: MapboxMap) {
        this.mapbox = mapbox

        val remotePresenter =
            RemotePresenter(disposable)

        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            this.style = style
            val markerOption = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLng)
                .setId("me-${System.currentTimeMillis()}")
                .build(context!!)

            val marker = mapbox.addMarker(markerOption).get("me")

            mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.0))

            remotePresenter.getDriversActive { drivers ->
                logi("driver found --> ${drivers?.size}")
                drivers?.forEachIndexed { index, driver ->
                    if (driver.position != null) {
                        val latLngDriver = LatLng(driver.position!!.lat!!, driver.position!!.lon!!)
                        val rotation = driver.position?.angle

                        val uniqueId = "${driver.id}-${System.currentTimeMillis()}"

                        logi("driver location --> $latLngDriver, id --> ${driver.id}, rotation -> $rotation from ${driver.position?.angle}")

                        val markerDriverOption = MarkerOptions.Builder()
                            .setId(uniqueId)
                            .setIcon(R.drawable.mapbox_marker_icon_default, true)
                            .setPosition(latLngDriver)
                            .setRotation(rotation)
                            .build(context)

                        mapbox.addMarker(markerDriverOption).get(uniqueId)
                    }
                }
            }

            layer.invoke(mapbox, marker)
        }
    }

    fun setPaddingBottom(padding: Int) {
        mapbox.uiSettings.setLogoMargins(30, 30, 30,(context!!.dp(padding)) + 30)

        val position = CameraPosition.Builder()
            .target(startLatLng)
            .zoom(17.0)
            .padding(0.0, 0.0, 0.0,  padding.toDouble())
            .build()

        mapbox.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    override fun onComplete() {
        super.onComplete()
        style.layers.clear()
    }


}