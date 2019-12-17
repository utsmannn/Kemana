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

package com.utsman.kemana.driver.fragment.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.calculateDistanceKm
import com.utsman.kemana.base.calculatePricing
import com.utsman.kemana.base.toast
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.impl.presenter.OrderInterface
import com.utsman.kemana.driver.impl.view.IOrderView
import com.utsman.kemana.driver.presenter.OrderPresenter
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.place.PlacePresenter
import com.utsman.kemana.remote.place.Places
import kotlinx.android.synthetic.main.bottom_sheet_frg_pickup.view.*

class PickupBottomSheet(
    private val orderData: OrderData
) : RxFragment(), IOrderView {

    private val passenger by lazy {
        orderData.attribute.passenger
    }

    private val startPlace by lazy {
        orderData.from
    }

    private val destPlace by lazy {
        orderData.to
    }

    private lateinit var placePresenter: PlacePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_pickup, container, false)
        placePresenter = PlacePresenter(composite)

        val startLat = startPlace?.geometry?.get(0)!!
        val startLon = startPlace?.geometry?.get(1)!!

        val destLat = destPlace?.geometry?.get(0)!!
        val destLon = destPlace?.geometry?.get(1)!!

        val from = "$startLat,$startLon"
        val to = "$destLat,$destLon"

        placePresenter.getPolyline(from, to) { poly ->
            setupView(v, poly?.distance)
        }


        return v
    }

    private fun setupView(v: View, distance: Double?) {
        v.text_name_passenger?.text = passenger?.name
        v.text_price.text = distance?.calculatePricing()
        v.text_distance.text = distance?.calculateDistanceKm()

        v.text_to_location.text = destPlace?.placeName
    }

    override fun onPickup(places: Places?) {
        view?.text_current_to_location?.text = places?.placeName
    }

    override fun onTake(places: Places?) {
        view?.text_current_to_location?.text = places?.placeName
    }

    override fun onArrive(places: Places?) {
        view?.text_current_to_location?.text = "Selesai"
    }
}