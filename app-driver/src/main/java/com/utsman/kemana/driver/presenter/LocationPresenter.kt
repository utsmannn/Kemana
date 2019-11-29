package com.utsman.kemana.driver.presenter

import android.content.Context
import android.location.Location
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.driver.impl.ILocationUpdateView
import com.utsman.kemana.driver.impl.ILocationView
import com.utsman.kemana.driver.impl.LocationInterface
import com.utsman.smartmarker.location.LocationUpdateListener
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox

class LocationPresenter(private val context: Context) : LocationInterface {

    private lateinit var locationWatcher: LocationWatcher

    override fun initLocation(iLocationView: ILocationView) {
        locationWatcher = LocationWatcher(context)

        locationWatcher.getLocation { location ->
            iLocationView.onLocationReady(location.toLatLngMapbox())
        }
    }

    override fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView) {
        locationWatcher.getLocationUpdate(object : LocationUpdateListener {
            override fun newLocation(newLocation: Location) {
                iLocationUpdateView.onLocationUpdate(newLocation.toLatLngMapbox())
                logi("location update started")
            }

            override fun oldLocation(oldLocation: Location) {

            }

            override fun failed(throwable: Throwable) {
                loge("location update failed")
                throwable.printStackTrace()
            }
        })
    }

    override fun onDestroy() {
        locationWatcher.stopLocationWatcher()
    }
}