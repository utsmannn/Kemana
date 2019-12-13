package com.utsman.kemana.driver.presenter

import android.content.Context
import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.driver.impl.view.ILocationUpdateView
import com.utsman.kemana.driver.impl.view.ILocationView
import com.utsman.kemana.driver.impl.presenter.LocationInterface
import com.utsman.smartmarker.location.LocationUpdateListener
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox

class LocationPresenter(private val context: Context) :
    LocationInterface {

    private lateinit var locationWatcher: LocationWatcher
    private var nowLatLng = LatLng()

    override fun initLocation(iLocationView: ILocationView) {
        locationWatcher = LocationWatcher(context)

        locationWatcher.getLocation { location ->
            iLocationView.onLocationReady(location.toLatLngMapbox())
        }
    }

    override fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView) {
        locationWatcher.getLocationUpdate(LocationWatcher.Priority.LOW, object : LocationUpdateListener {
            override fun newLocation(newLocation: Location) {
                iLocationUpdateView.onLocationUpdate(newLocation.toLatLngMapbox())
                logi("location update started")
            }

            override fun oldLocation(oldLocation: Location) {
                iLocationUpdateView.onLocationUpdateOld(oldLocation.toLatLngMapbox())
            }

            override fun failed(throwable: Throwable) {
                loge("location update failed")
                throwable.printStackTrace()
            }
        })
    }

    override fun getNowLocation(): LatLng {
        locationWatcher.getLocation {
            nowLatLng = it.toLatLngMapbox()
        }

        return nowLatLng
    }

    override fun onDestroy() {
        locationWatcher.stopLocationWatcher()
    }
}