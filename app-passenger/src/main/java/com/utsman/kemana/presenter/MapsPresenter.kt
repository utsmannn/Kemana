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

import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.impl.view.IMapView
import com.utsman.kemana.impl.presenter.MapsInterface
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

class MapsPresenter(private val iMapView: IMapView) :
    MapsInterface {
    override fun mapStart(startLatLng: LatLng) {
        iMapView.mapStart(startLatLng)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        iMapView.mapReady(start, destination, polyline)
    }

    override fun mapOrder(orderData: OrderData) {
        iMapView.mapPickup(orderData)
    }

    override fun failedServerConnection() {
        iMapView.failedServerConnection()
    }

    override fun dispose() {
        iMapView.dispose()
    }
}