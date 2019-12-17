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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

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

    override fun startLocationUpdate(iLocationUpdateView: ILocationUpdateView): Disposable {
        return Observable.just(iLocationUpdateView)
            .subscribeOn(Schedulers.io())
            .doOnNext {
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
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