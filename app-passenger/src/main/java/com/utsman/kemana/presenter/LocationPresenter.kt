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