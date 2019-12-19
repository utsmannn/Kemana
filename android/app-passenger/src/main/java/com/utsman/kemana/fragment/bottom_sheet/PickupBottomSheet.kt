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

package com.utsman.kemana.fragment.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.driver.Driver
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.place.PlacePresenter
import kotlinx.android.synthetic.main.bottom_sheet_frg_pickup.view.*
import org.json.JSONObject

class PickupBottomSheet(private val orderData: OrderData, private val messagingPresenter: MessagingPresenter) : RxFragment() {

    /*private val driver by lazy {
        orderData.attribute.driver
    }*/

    private val driverId by lazy {
        orderData.attribute.driver?.id
    }

    private val startPlace by lazy {
        orderData.from
    }

    private val destPlace by lazy {
        orderData.to
    }

    private lateinit var placePresenter: PlacePresenter
    private lateinit var remotePresenter: RemotePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_pickup, container, false)
        placePresenter = PlacePresenter(composite)
        remotePresenter = RemotePresenter(composite)

        getDriverById { driver ->
            setupView(driver, v)

            v.btn_cancel.setOnClickListener {
                val jsonObject = JSONObject()
                val jsonEmpty = JSONObject()

                jsonObject.apply {
                    put("type", Type.ORDER_CANCEL)
                    put("data", jsonEmpty)
                }

                Rabbit.getInstance()?.publishTo(driver?.email!!, jsonObject) {
                    toast("error, try again")
                }

                messagingPresenter.orderCancel()
            }
        }

        return v
    }

    private fun getDriverById(ok: (Driver?) -> Unit) {
        remotePresenter.getRegisteredDriverById(orderData.attribute.driver?.id) {
            logi("driver is --> $it")

            if (it != null) {
                ok.invoke(it)
            }
        }
    }

    private fun setupView(driver: Driver?, v: View) {

        val startLat = startPlace?.geometry?.get(0)!!
        val startLon = startPlace?.geometry?.get(1)!!

        val destLat = destPlace?.geometry?.get(0)!!
        val destLon = destPlace?.geometry?.get(1)!!

        val from = "$startLat,$startLon"
        val to = "$destLat,$destLon"

        v.text_name_driver.text = driver?.name
        v.text_vehicles_type.text = driver?.attribute?.vehiclesType
        v.text_vehicles_num.text = driver?.attribute?.vehiclesPlat

        placePresenter.getPolyline(from, to) { poly ->
            v.text_price.text = poly?.distance?.calculatePricing()
            v.text_distance.text = poly?.distance?.calculateDistanceKm()
        }

        placePresenter.getAddress(to) { place ->
            v.text_to_location.text = place?.placeName
        }

        Glide.with(this)
            .load(driver?.photoUrl)
            .circleCrop()
            .into(v.img_driver_profile)

    }
}