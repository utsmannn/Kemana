/*
 * Copyright 2019 Muhammad Utsman
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

package com.utsman.kemana.driver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.ext.calculateDistanceKm
import com.utsman.kemana.base.ext.calculatePricing
import com.utsman.kemana.base.ext.loadCircleUrl
import com.utsman.kemana.base.rx.RxFragment
import com.utsman.kemana.driver.R
import com.utsman.kemana.maputil.toLocation
import com.utsman.kemana.message.OrderData
import com.utsman.kemana.places.PlaceRouteApp
import kotlinx.android.synthetic.main.fragment_pick_up.*

class PickupFragment(private val orderData: OrderData) : RxFragment() {

    private lateinit var placeRouteApp: PlaceRouteApp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeRouteApp = PlaceRouteApp(compositeDisposable)

        val fromLatLng = LatLng(orderData.fromLat, orderData.fromLng)
        val toLatLng = LatLng(orderData.toLat, orderData.toLng)

        placeRouteApp.getMyAddress(fromLatLng.toLocation())
            .observe(context as LifecycleOwner, Observer {
                val addressName = it.place_name
                text_from_location.text = addressName
            })

        placeRouteApp.getMyAddress(toLatLng.toLocation())
            .observe(context as LifecycleOwner, Observer {
                val addressName = it.place_name
                text_to_location.text = addressName
            })

        val distanceKm = orderData.distance.calculateDistanceKm()
        val priceRp = orderData.distance.calculatePricing()

        img_user_customer.loadCircleUrl(orderData.userImg)
        text_user_customer.text = orderData.username

        //text_distance.text = distanceKm
        //text_pricing.text = priceRp

    }
}