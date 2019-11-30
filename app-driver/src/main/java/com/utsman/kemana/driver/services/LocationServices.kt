package com.utsman.kemana.driver.services

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.NotifyState
import com.utsman.kemana.base.RxService
import com.utsman.kemana.base.listen
import com.utsman.kemana.base.logi
import com.utsman.kemana.driver.impl.ILocationUpdateView
import com.utsman.kemana.driver.impl.ILocationView
import com.utsman.kemana.driver.presenter.LocationPresenter
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import com.utsman.kemana.remote.*
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider

class LocationServices : RxService(), ILocationView, ILocationUpdateView {

    private lateinit var locationPresenter: LocationPresenter
    private lateinit var remotePresenter: RemotePresenter

    private var defaultLatLng = LatLng()
    private var driver: Driver? = null
    private var driverId: String? = null

    private var ready = MutableLiveData<Boolean>()
    private var onActive = MutableLiveData<Boolean>()
    //private val livePosition = MutableLiveData<Position>()

    override fun onCreate() {
        super.onCreate()
        ready.postValue(false)
        onActive.postValue(false)
        //livePosition.postValue(null)

        locationPresenter = LocationPresenter(this)
        locationPresenter.initLocation(this)

        remotePresenter = RemotePresenter()

        Notify.listen(Driver::class.java, NotifyProvider(), Consumer {
            logi("receiving driver model")
            driver = it
            ready.postValue(true)

            Notify.send(NotifyState(NotifyState.READY))
        })

        ready.observeForever {
            if (it) {
                notifyListener()
            }
        }
    }

    private fun notifyListener() {
        Notify.listen { state ->
            logi("location update started, state is --> $state")

            when (state) {
                NotifyState.UPDATE_LOCATION -> {
                    locationPresenter.startLocationUpdate(this)
                }

                RemoteState.INSERT_DRIVER -> {
                    remotePresenter.insertDriver(driver!!) {
                        driverId = it?.id
                        logi("driver inserted --> id: ${it?.id}")
                    }
                }

                RemoteState.ALL_DRIVER -> {
                    remotePresenter.getDriversActive {
                        logi(it.toString())
                    }
                }

                RemoteState.DRIVER -> {
                    remotePresenter.getDriver(driver?.id!!) {

                    }
                }

                RemoteState.EDIT_DRIVER -> {
                    onActive.postValue(true)
                }

                RemoteState.STOP_EDIT_DRIVER -> {
                    onActive.postValue(false)
                }

                RemoteState.DELETE_DRIVER -> {
                    logi("deleted --> ${driverId.toString()}")
                    driverId?.let { id ->
                        remotePresenter.deleteDriver(id) { status ->
                            logi("driver removed --> $status")
                            //onActive.postValue(false)
                        }
                    }
                }
            }
        }
    }

    private fun getDriverById(id: String): Driver? {
        return remotePresenter.getDriver(id)
    }

    override fun onLocationReady(latLng: LatLng) {
        val locationSubs = LocationSubs(latLng)
        Notify.send(locationSubs)

    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        logi("location updated")
        val updateLocationSubs = UpdateLocationSubs(newLatLng)
        Notify.send(updateLocationSubs)

        onActive.observeForever { active ->
            logi("activated status --> $active --> $driverId")
            if (active) {
                driverId?.let { id ->
                    remotePresenter.editDriver(id, Position(newLatLng.latitude, newLatLng.longitude)) {
                        logi("driver edited --> id: ${it?.id}, position: ${it?.position.toString()}")
                    }
                }
            }
        }
    }

    override fun onLocationUpdateOld(oldLatLng: LatLng) {
        defaultLatLng = oldLatLng
    }

    override fun getNowLocation() {
        logi("anjay")
    }
}