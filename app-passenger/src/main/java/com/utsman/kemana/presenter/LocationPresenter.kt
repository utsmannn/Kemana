package com.utsman.kemana.presenter

import android.content.Context
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.impl.view.ILocationView
import com.utsman.kemana.impl.presenter.LocationInterface
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox

class LocationPresenter(private val context: Context) :
    LocationInterface {

    private lateinit var locationWatcher: LocationWatcher

    override fun initLocation(iLocationView: ILocationView) {
        locationWatcher = LocationWatcher(context)

        locationWatcher.getLocation { location ->
            iLocationView.onLocationReady(location.toLatLngMapbox())
        }
    }

    override fun onDestroy() {
        locationWatcher.stopLocationWatcher()
    }
}