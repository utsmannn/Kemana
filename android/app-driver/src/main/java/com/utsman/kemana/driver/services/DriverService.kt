package com.utsman.kemana.driver.services

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.utsman.feature.base.*
import com.utsman.feature.remote.instance.UserInstance
import com.utsman.feature.remote.model.Position
import com.utsman.feature.remote.model.User
import com.utsman.kemana.driver.subscriber.NotifyUpdateLocator
import com.utsman.kemana.driver.subscriber.OnlineUpdater
import com.utsman.smartmarker.location.LocationUpdateListener
import com.utsman.smartmarker.location.LocationWatcher
import com.utsman.smartmarker.mapbox.toLatLngMapbox
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider

class DriverService : RxService() {

    private val locationWatcher by lazy {
        LocationWatcher(this)
    }

    private val userInstance by lazy {
        UserInstance.create()
    }

    private val email by lazy {
        Preferences_getEmail()
    }

    private val id by lazy {
        Preferences_getId()
    }

    private val isOnline = MutableLiveData<Boolean>()
    private var isAddedOnline = false


    override fun onCreate() {
        super.onCreate()
        isOnline.postValue(false)
        val observableUser = userInstance.getUser(email, id, "driver")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.data }
            .subscribe({
                setupContent(it)
            }, {
                loge(it.message)
                it.printStackTrace()
            })

        composite.add(observableUser)

        Notify.listen(OnlineUpdater::class.java, NotifyProvider(), Consumer {
            isOnline.postValue(it.isOnline)
            logi("anjayy --> from driver service")
        })
    }

    private fun setupContent(user: User) {
        locationWatcher.getLocationUpdate(LocationWatcher.Priority.JEDI, object : LocationUpdateListener {
            override fun oldLocation(oldLocation: Location?) {
                // old location
            }

            override fun newLocation(newLocation: Location) {
                val notifyUpdateLocator = NotifyUpdateLocator(newLocation.toLatLngMapbox())
                Notify.send(notifyUpdateLocator)

                isOnline.observeForever { order ->
                    if (order) {
                        logi("anjay -> is online")
                        val position = Position(newLocation.latitude, newLocation.longitude)
                        user.position = position
                        val observableSaveUser = userInstance.saveUser(email, "driver_active", user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                isAddedOnline = true
                            }

                        composite.add(observableSaveUser)
                    } else {
                        logi("anjay -> is offline")

                        if (isAddedOnline) {
                            val observableDeleteUser = userInstance.deleteUser(email, id, "driver_active")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    isAddedOnline = false
                                }

                            composite.add(observableDeleteUser)
                        }
                    }
                }
            }

            override fun failed(throwable: Throwable?) {
                throwable?.printStackTrace()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        composite.clear()
    }
}