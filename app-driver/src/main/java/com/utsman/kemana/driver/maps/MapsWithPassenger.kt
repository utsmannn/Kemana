package com.utsman.kemana.driver.maps

import androidx.fragment.app.FragmentActivity
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.auth.User
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.MarkerUtil
import io.reactivex.disposables.CompositeDisposable

class MapsWithPassenger(
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

            val markerUtil = MarkerUtil(activity)

            //val markerDriver = MarkerUtil(activity, driverLatLng)
            markerUtil.addMarker("driver", style, R.drawable.ic_marker_driver, true, driverLatLng)

            //val markerPassenger = MarkerUtil(activity, passengerLatLng)
            markerUtil.addMarker("passenger", style, R.drawable.ic_person_location, true, passengerLatLng)

            val cameraPosition = CameraUpdateFactory.newLatLngBounds(
                position, 200, 200, 200, paddingBottom + 200)
            mapboxMap.animateCamera(cameraPosition)

        }
    }
}