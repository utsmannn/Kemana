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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.place.PlacePresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses
import com.utsman.kemana.subscriber.LocationSubs
import com.utsman.recycling.extentions.NetworkState
import com.utsman.recycling.setupAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.bottoh_sheet_location_picker.view.*
import kotlinx.android.synthetic.main.bottom_sheet_frg_main.view.*
import kotlinx.android.synthetic.main.item_location.view.*
import java.util.concurrent.TimeUnit

class MainBottomSheet(
    private val mapsPresenter: MapsPresenter,
    private val messagingPresenter: MessagingPresenter,
    private var startLatLng: LatLng
) : RxFragment() {

    private val placePresenter = PlacePresenter(composite)
    private var destinationLatLng = LatLng()

    private var startPlace: Places? = null
    private var destinationPlace: Places? = null

    private var polyline: PolylineResponses? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_main, container, false)

        v.detail_pricing_container.visibility = View.GONE
        v.btn_order.isEnabled = false
        v.text_from.text = "Your location"

        placePresenter.getAddress("${startLatLng.latitude},${startLatLng.longitude}") { place ->
            if (place != null) {
                startPlace = place
                val name = place.placeName
                v.text_from.text = "$name (current location)"
            } else {
                mapsPresenter.failedServerConnection()
            }
        }

        v.container_from.setOnClickListener {
            showLocationPicker { place, latLng ->
                startLatLng = latLng
                startPlace = place

                v.text_from.text = place?.placeName

                if (startPlace != null && destinationPlace != null) {

                    val from = "${latLng.latitude},${latLng.longitude}"
                    val to = "${destinationLatLng.latitude},${destinationLatLng.longitude}"
                    logi("request is --> ${startPlace?.geometry} -- ${destinationPlace?.geometry}")

                    placePresenter.getPolyline(from, to) { poly ->
                        v.btn_order.isEnabled = true
                        polyline = poly
                        mapsPresenter.mapReady(startPlace!!, destinationPlace!!, poly).apply {
                            setupPricing(v, poly)
                        }
                    }
                }
            }
        }

        v.container_to.setOnClickListener {
            showLocationPicker { place, latLng ->
                destinationPlace = place
                destinationLatLng = latLng

                v.text_to.text = place?.placeName
                logi("$startPlace -- $destinationPlace")

                if (startPlace != null && destinationPlace != null) {
                    logi("request is --> ${startPlace?.geometry} -- ${destinationPlace?.geometry}")

                    val from = "${startLatLng.latitude},${startLatLng.longitude}"
                    val to = "${latLng.latitude},${latLng.longitude}"

                    placePresenter.getPolyline(from, to) { poly ->
                        polyline = poly
                        v.btn_order.isEnabled = true

                        mapsPresenter.mapReady(startPlace!!, destinationPlace!!, poly).apply {
                            setupPricing(v, poly)
                        }
                    }
                }
            }
        }

        v.btn_order.setOnClickListener {
            if (startPlace != null && destinationPlace != null && polyline != null) {
                messagingPresenter.findDriver(startPlace!!, destinationPlace!!, polyline!!)
            }
        }

        return v
    }

    private fun showLocationPicker(placeName: (Places?, LatLng) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        val viewDialog =
            LayoutInflater.from(context).inflate(R.layout.bottoh_sheet_location_picker, null)
        bottomSheetDialog.setContentView(viewDialog)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            viewDialog.input_location.requestFocus()
        }

        viewDialog?.rv_location?.setupAdapter<Places>(R.layout.item_location) { adapter, context, list ->
            list?.clear()
            adapter.notifyDataSetChanged()
            bind { itemView, position, item ->
                itemView.item_text_place_title.text = item?.placeName
                itemView.item_text_place_address.text = item?.addressName

                itemView.setOnClickListener {
                    val latLng = LatLng(item!!.geometry!![0]!!, item.geometry!![1]!!)
                    placeName.invoke(item, latLng)
                    bottomSheetDialog.dismiss()
                }
            }

            addLoader(R.layout.item_loader) {
                idLoader = R.id.progress_circular
                idTextError = R.id.error_text_view
            }

            val disposable = viewDialog.input_location.afterTextChangeEvents()
                .subscribeOn(Schedulers.io())
                .map { it.editable.toString() }
                .filter { it.length > 2 }
                .doOnNext {
                    list?.clear()
                    adapter.notifyDataSetChanged()
                }
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    submitNetworkState(NetworkState.LOADING)
                }
                .subscribe({
                    placePresenter.search(
                        it,
                        "${startLatLng.latitude},${startLatLng.longitude}"
                    ) { places ->
                        places?.let { results ->
                            submitNetworkState(NetworkState.LOADED)
                            submitList(results.take(8))
                        }
                    }
                }, {
                    it.printStackTrace()
                })

            composite.add(disposable)
        }

        bottomSheetDialog.show()
    }

    private fun setupPricing(v: View, poly: PolylineResponses?) {
        v.text_price.text = poly?.distance?.calculatePricing()
        v.text_distance.text = poly?.distance?.calculateDistanceKm()
    }

    fun pricingVisible() {
        view?.detail_pricing_container?.visibility = View.VISIBLE
        view?.btn_order?.isEnabled = true
    }

    fun pricingGone() {
        view?.detail_pricing_container?.visibility = View.GONE
        view?.text_to?.text = "Kemana?"
        view?.btn_order?.isEnabled = false
    }
}