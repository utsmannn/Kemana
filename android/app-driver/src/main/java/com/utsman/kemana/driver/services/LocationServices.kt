/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.driver.services

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.base.*
import com.utsman.kemana.driver.impl.view.ILocationUpdateView
import com.utsman.kemana.driver.impl.view.ILocationView
import com.utsman.kemana.driver.presenter.LocationPresenter
import com.utsman.kemana.driver.subscriber.*
import com.utsman.kemana.remote.driver.Driver
import com.utsman.kemana.remote.driver.Position
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.driver.RemoteState
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import org.json.JSONObject

class LocationServices : RxService(),
    ILocationView,
    ILocationUpdateView {

    private lateinit var locationPresenter: LocationPresenter
    private lateinit var remotePresenter: RemotePresenter

    private var updateLocationDisposable: Disposable? = null
    private var updateToPassenger: Disposable? = null

    private var defaultLatLng = LatLng()
    private var driver: Driver? = null
    private var driverId: String? = null

    private var ready = MutableLiveData<Boolean>()
    private var onActive = MutableLiveData<Boolean>()
    private var email: String? = null
    private val livePosition = MutableLiveData<Position>()

    private var emailPassenger: String? = null

    private val observerPosition by lazy {
        Observer<Position> { t ->
            if (email != null) {
                remotePresenter.editDriverByEmail(email!!, t) {
                    logi("driver edited --> id: ${it?.id}, position: ${it?.position.toString()}")
                }
            }
        }
    }

    @SuppressLint("AuthLeak")
    override fun onCreate() {
        super.onCreate()
        ready.postValue(false)
        onActive.postValue(false)

        locationPresenter = LocationPresenter(this)
        locationPresenter.initLocation(this)

        remotePresenter = RemotePresenter(composite)

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

        Notify.listen(NotifyState::class.java, NotifyProvider(), Consumer { value ->
            logi("notify receiving")
            logi("location update started, state is --> ${value.state}")

            when (value.state) {
                NotifyState.UPDATE_LOCATION -> {
                    updateLocationDisposable = locationPresenter.startLocationUpdate(this)
                }

                NotifyState.STOP_UPDATE_LOCATION -> {
                    updateLocationDisposable?.dispose()
                }

                RemoteState.INSERT_DRIVER -> {
                    livePosition.removeObserver(observerPosition)

                    remotePresenter.insertDriver(driver!!) { success, driver ->
                        logi("driver == ${driver.toString()}, success == $success")

                        if (!success) {
                            logi("driver unready")
                            Notify.send(NotifyState(NotifyState.DRIVER_UNREADY))
                        } else {
                            logi("driver ready")
                            Notify.send(NotifyState(NotifyState.DRIVER_READY))
                            email = driver!!.email
                            driverId = driver.id
                            logi("driver inserted --> id: ${driver.id}")
                            onActive.postValue(true)

                            livePosition.observeForever(observerPosition)

                            setupRabbit(email!!)
                        }
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
                    onActive.postValue(false)

                    logi("deleted --> ${driverId.toString()}")

                    if (email != null) {
                        livePosition.removeObserver(observerPosition).apply {
                            logi("observer removed")

                            remotePresenter.deleteDriverByEmail(email!!) { status ->
                                logi("driver removed --> $status")
                                email = null
                            }
                        }
                    }
                }
            }

        }, Consumer {
            loge(it.localizedMessage)
            it.printStackTrace()
        })
    }

    private fun setupRabbit(email: String) {
        Rabbit.setID(email)

        logi("start setup rabbit")

        Rabbit.getInstance()?.listen { from, body ->
            logi("data is coming --> $body")

            val type = body.getInt("type")
            val data = body.getJSONObject("data")

            when (type) {
                Type.ORDER_REQUEST -> {
                    val objectSubs = ObjectOrderSubs(data)
                    Notify.send(objectSubs)
                    logi("from $from --> $body")
                }
                Type.ORDER_CANCEL -> {
                    val orderCancel = OrderCancelSubs(true)
                    Notify.send(orderCancel)
                    emailPassenger = null
                    updateToPassenger?.dispose()
                }
            }
        }
    }

    override fun onLocationReady(latLng: LatLng) {
        val locationSubs = LocationSubs(latLng)
        Notify.send(locationSubs)
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        logi("location updated")
        val updateLocationSubs = UpdateLocationSubs(newLatLng)
        Notify.send(updateLocationSubs)

        Notify.listen(RotationSubs::class.java, NotifyProvider(), Consumer {
            val position = Position(
                newLatLng.latitude,
                newLatLng.longitude,
                it.double
            )

            livePosition.postValue(position)
        })

        messagingForPassenger(newLatLng)
    }

    private fun messagingForPassenger(newLatLng: LatLng) {
        Notify.listen(TrackerPassengerSubs::class.java, NotifyProvider(), Consumer {
            logi("email passenger is --> ${it.email}")
            emailPassenger = it.email
        })

        logi("start checking passenger on --> $emailPassenger")
        if (emailPassenger != null) {
            logi("sending tracking")
            val jsonLatLon = JSONObject()
            jsonLatLon.put("lat", newLatLng.latitude)
            jsonLatLon.put("lon", newLatLng.longitude)

            val jsonObject = JSONObject()
            jsonObject.apply {
                put("type", Type.TRACKING)
                put("data", jsonLatLon)
            }

            updateToPassenger = Rabbit.getInstance()?.publishTo(emailPassenger!!, jsonObject) {
                toast("error --> ${it.localizedMessage}")
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