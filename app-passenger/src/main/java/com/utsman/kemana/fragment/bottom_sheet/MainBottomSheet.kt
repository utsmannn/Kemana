package com.utsman.kemana.fragment.bottom_sheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.R
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.logi
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.remote.place.PlacePresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.subscriber.LocationSubs
import com.utsman.recycling.extentions.NetworkState
import com.utsman.recycling.setupAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import isfaaghyth.app.notify.EventProvider
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.bottoh_sheet_location_picker.view.*
import kotlinx.android.synthetic.main.bottom_sheet_frg_main.view.*
import kotlinx.android.synthetic.main.item_location.view.*
import java.util.concurrent.TimeUnit

class MainBottomSheet(private val mapsPresenter: MapsPresenter) : RxFragment() {

    private val placePresenter = PlacePresenter(compositeDisposable)
    private var latLng = LatLng()

    private var startPlace: Places? = null
    private var destinationPlace: Places? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_main, container, false)

        Notify.listen(LocationSubs::class.java, NotifyProvider(), Consumer {
            this.latLng = it.latLng

            placePresenter.getAddress("${latLng.latitude},${latLng.longitude}") { place ->
                startPlace = place
                val name = place?.placeName
                v.text_from.text = "$name (current location)"
            }
        })

        v.btn_order.isEnabled = false

        v.container_from.setOnClickListener {
            showLocationPicker {
                startPlace = it
                v.text_from.text = it?.placeName

                if (startPlace != null && destinationPlace != null) {
                    mapsPresenter.mapReady(startPlace!!, destinationPlace!!)
                }
            }
        }

        v.container_to.setOnClickListener {
            showLocationPicker {
                destinationPlace = it
                v.text_to.text = it?.placeName
                logi("$startPlace -- $destinationPlace")

                if (startPlace != null && destinationPlace != null) {
                    mapsPresenter.mapReady(startPlace!!, destinationPlace!!)
                }
            }
        }
        return v
    }

    private fun showLocationPicker(placeName: (Places?) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        val viewDialog = LayoutInflater.from(context).inflate(R.layout.bottoh_sheet_location_picker, null)
        bottomSheetDialog.setContentView(viewDialog)

        viewDialog?.rv_location?.setupAdapter<Places>(R.layout.item_location) { adapter, context, list ->
            list?.clear()
            adapter.notifyDataSetChanged()
            bind { itemView, position, item ->
                itemView.item_text_place_title.text = item?.placeName
                itemView.item_text_place_address.text = item?.addressName

                itemView.setOnClickListener {
                    placeName.invoke(item)
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
                .debounce(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    submitNetworkState(NetworkState.LOADING)
                }
                .subscribe({
                    placePresenter.search(it, "${latLng.latitude},${latLng.longitude}") { places ->
                        places?.let {  results ->
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
}