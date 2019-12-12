package com.utsman.kemana.fragment.bottom_sheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.presenter.MapsPresenter
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

class MainBottomSheet(private val mapsPresenter: MapsPresenter) : RxFragment() {

    private val placePresenter = PlacePresenter(compositeDisposable)
    private var startLatLng = LatLng()
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
        v.text_from.text = "Your location"

        Notify.listen(LocationSubs::class.java, NotifyProvider(), Consumer {
            startLatLng = it.latLng

            placePresenter.getAddress("${startLatLng.latitude},${startLatLng.longitude}") { place ->
                startPlace = place
                val name = place?.placeName
                v.text_from.text = "$name (current location)"
            }
        })

        v.btn_order.isEnabled = false

        v.container_from.setOnClickListener {
            showLocationPicker { place, latLng ->
                startLatLng = latLng
                startPlace = place

                v.text_from.text = place?.placeName

                if (startPlace != null && destinationPlace != null) {
                    val from = "${startLatLng.latitude},${startLatLng.longitude}"
                    val to = "${destinationLatLng.latitude},${destinationLatLng.longitude}"

                    placePresenter.getPolyline(from, to) { poly ->
                        polyline = poly
                        mapsPresenter.mapReady(startPlace!!, destinationPlace!!, poly).apply {
                            setupPricing(v, poly)
                        }
                    }

                    v.btn_order.isEnabled = true
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
                    val from = "${startLatLng.latitude},${startLatLng.longitude}"
                    val to = "${latLng.latitude},${latLng.longitude}"

                    placePresenter.getPolyline(from, to) { poly ->
                        polyline = poly
                        mapsPresenter.mapReady(startPlace!!, destinationPlace!!, poly).apply {
                            setupPricing(v, poly)
                        }
                    }

                    v.btn_order.isEnabled = true
                }
            }
        }

        v.btn_order.setOnClickListener {
            //toast("test rabbit is --> ${Rabbit.sent()}")

        }

        return v
    }

    private fun showLocationPicker(placeName: (Places?, LatLng) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        val viewDialog =
            LayoutInflater.from(context).inflate(R.layout.bottoh_sheet_location_picker, null)
        bottomSheetDialog.setContentView(viewDialog)

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
                            submitList(results)
                        }
                    }
                }, {
                    it.printStackTrace()
                })

            compositeDisposable.add(disposable)
        }

        bottomSheetDialog.show()
    }

    private fun setupPricing(v: View, poly: PolylineResponses) {
        v.detail_pricing_container.visibility = View.VISIBLE
        v.text_price.text = poly.distance?.calculatePricing()
        v.text_distance.text = poly.distance?.calculateDistanceKm()
    }
}