package com.utsman.kemana.maps

import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.utsman.kemana.R
import com.utsman.kemana.auth.User
import com.utsman.kemana.maputil.MarkerUtil
import io.reactivex.disposables.CompositeDisposable

class MapsWithDriver(
    private val activity: FragmentActivity,
    private val disposable: CompositeDisposable,
    private val driver: User,
    private val user: User,
    private val ready: (driver: User) -> Unit
) : OnMapReadyCallback {

    private var paddingBottom = 0

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            val driverLatLng = LatLng(driver.lat!!, driver.lon!!)
            val passengerLatLng = LatLng(user.lat!!, user.lon!!)

            val position = LatLngBounds.Builder()
                .include(driverLatLng)
                .include(passengerLatLng)
                .build()

            val markerDriver = MarkerUtil(activity, driverLatLng)
            markerDriver.addMarker("driver", style, R.drawable.ic_marker_driver, true, symbolLayer = {
                it.withProperties(PropertyFactory.iconRotate(user.angle!!.toFloat()))
            }) {
                return@addMarker driverLatLng
            }

            val markerPassenger = MarkerUtil(activity, passengerLatLng)
            markerPassenger.addMarker("passenger", style, R.drawable.ic_person_location, true) {
                return@addMarker passengerLatLng
            }

            val cameraPosition = CameraUpdateFactory.newLatLngBounds(
                position, 200, 200, 200, paddingBottom + 200)
            mapboxMap.animateCamera(cameraPosition)

        }
    }
}