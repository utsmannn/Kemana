package com.utsman.kemana.driver.services

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.NotifyState
import com.utsman.kemana.base.RxService
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.driver.impl.ILocationUpdateView
import com.utsman.kemana.driver.impl.ILocationView
import com.utsman.kemana.driver.presenter.LocationPresenter
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider

class LocationServices : RxService(), ILocationView, ILocationUpdateView {

    private lateinit var locationPresenter: LocationPresenter

    override fun onCreate() {
        super.onCreate()
        locationPresenter = LocationPresenter(this)
        locationPresenter.initLocation(this)

        Notify.listen(NotifyState::class.java, NotifyProvider(), Consumer { value ->
            logi("location update started")

            when (value.state) {
                NotifyState.UPDATE_LOCATION -> {
                    locationPresenter.startLocationUpdate(this)
                }
            }
        }, Consumer {
            loge(it.localizedMessage)
            it.printStackTrace()
        })
    }

    override fun onLocationReady(latLng: LatLng) {
        val locationSubs = LocationSubs(latLng)
        Notify.send(locationSubs)
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        val updateLocationSubs = UpdateLocationSubs(newLatLng)
        Notify.send(updateLocationSubs)
    }
}