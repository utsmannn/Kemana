package com.utsman.kemana.driver.services

import android.content.Intent
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.KEY
import com.utsman.kemana.base.RxService
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.driver.impl.ILocationUpdateView
import com.utsman.kemana.driver.impl.ILocationView
import com.utsman.kemana.driver.presenter.LocationPresenter
import com.utsman.kemana.driver.subscriber.LocationSubs
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider

class LocationServices : RxService(), ILocationView, ILocationUpdateView {

    private lateinit var locationPresenter: LocationPresenter

    override fun onCreate() {
        super.onCreate()
        locationPresenter = LocationPresenter(this)
        locationPresenter.initLocation(this)

        Notify.listen(Int::class.java, NotifyProvider(), Consumer { value ->
            logi("location update started")

            when (value) {
                KEY.UPDATE_LOCATION -> {
                    locationPresenter.startLocationUpdate(this)
                }
            }
        }, Consumer {
            loge(it.localizedMessage)
            it.printStackTrace()
        })
    }

    override fun locationReady(latLng: LatLng) {
        val updateLocationSubs = LocationSubs(latLng)
        Notify.send(updateLocationSubs)
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        val updateLocationSubs = LocationSubs(newLatLng)
        Notify.send(updateLocationSubs)
    }
}