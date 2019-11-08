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
import com.utsman.kemana.maputil.EventTracking
import com.utsman.smartmarker.mapbox.MarkerLayer
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker

class MapsPickup(
    private val activity: FragmentActivity,
    private val driver: User,
    private val user: User,
    private val ready: (driver: User) -> Unit
) : OnMapReadyCallback {

    private var paddingBottom = 0
    private lateinit var markerLayer: MarkerLayer

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

            val markerOptionDriver = MarkerOptions.Builder()
                .addPosition(driverLatLng)
                .addIcon(R.drawable.ic_marker_driver, true)
                .setId("driver")
                .build(activity)

            val markerOptionsPassenger = MarkerOptions.Builder()
                .addPosition(passengerLatLng)
                .addIcon(R.drawable.ic_person_location, true)
                .setId("passenger")
                .build(activity)

            ready.invoke(driver)

            markerLayer = mapboxMap.addMarker(markerOptionDriver, markerOptionsPassenger)

            val cameraPosition = CameraUpdateFactory.newLatLngBounds(
                position, 200, 200, 200, paddingBottom + 200)
            mapboxMap.animateCamera(cameraPosition)

        }
    }

    fun onEventTracker(eventTracking: EventTracking) {
        val markerDriver = markerLayer.get("driver")
        markerDriver?.moveMarkerSmoothly(eventTracking.latLngUpdater.newLatLng)
    }
}