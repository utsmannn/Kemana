package com.utsman.kemana.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.feature.base.*
import com.utsman.feature.remote.instance.DirectionInstance
import com.utsman.feature.remote.instance.PlaceInstance
import com.utsman.feature.remote.model.Direction
import com.utsman.feature.remote.model.Place
import com.utsman.kemana.R
import com.utsman.kemana.impl.NormalControlImpl
import com.utsman.recycling.extentions.NetworkState
import com.utsman.recycling.setupAdapter
import com.utsman.smartmarker.location.LocationWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.bottoh_sheet_location_picker.view.*
import kotlinx.android.synthetic.main.control_main.view.*
import kotlinx.android.synthetic.main.item_location.view.*
import java.util.concurrent.TimeUnit

class NormalControlFragment(private val normalControlImpl: NormalControlImpl) : RxFragment() {

    private val locationWatcher by lazy {
        LocationWatcher(context)
    }

    private val placeInstance = PlaceInstance.create()
    private val directionInstance = DirectionInstance.create()

    private var fromPlace: Place? = null
    private var toPlace: Place? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.control_main, container, false)
        locationWatcher.getLocation { location ->
            val from = listOf(location.latitude, location.longitude)
            val observablePlace = placeInstance.getCurrentPlace(from, HERE_API_KEY)
                .subscribeOn(Schedulers.io())
                .map { it.places[0] }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    fromPlace = it

                    v.text_from.text = fromPlace?.placeName
                }, {
                    it.printStackTrace()
                })

            composite.add(observablePlace)
        }

        v.container_from.setOnClickListener {
            showLocationPicker { place, latLng ->
                //startLatLng = latLng
                fromPlace = place

                v.text_from.text = place?.placeName

                if (fromPlace != null && toPlace != null) {
                    logi("request is --> ${fromPlace?.geometry} -- ${toPlace?.geometry}")

                    directionInstance.getDirection(fromPlace?.geometry, toPlace?.geometry, MAPBOX_TOKEN)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            setupPricing(v, it)
                        }, {
                            it.printStackTrace()
                        })
                }
            }
        }

        v.container_to.setOnClickListener {
            showLocationPicker { place, latLng ->
                toPlace = place

                v.text_to.text = place?.placeName

                if (fromPlace != null && toPlace != null) {
                    logi("request is --> ${fromPlace?.geometry} -- ${toPlace?.geometry}")

                    directionInstance.getDirection(fromPlace?.geometry, toPlace?.geometry, MAPBOX_TOKEN)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            setupPricing(v, it)
                        }, {
                            it.printStackTrace()
                        })
                }
            }
        }


        return v
    }

    private fun showLocationPicker(placeName: (Place?, LatLng) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val viewDialog =
            LayoutInflater.from(context).inflate(R.layout.bottoh_sheet_location_picker, null)
        bottomSheetDialog.setContentView(viewDialog)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            viewDialog.input_location.requestFocus()
        }

        viewDialog?.rv_location?.setupAdapter<Place>(R.layout.item_location) { adapter, context, list ->
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
                .subscribe({ query ->

                    placeInstance.searchPlace(query, fromPlace?.geometry, HERE_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .map { it.places }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            submitNetworkState(NetworkState.LOADED)
                            submitList(it.take(10))
                        }, {
                            it.printStackTrace()
                        })


                }, {
                    it.printStackTrace()
                })

            composite.add(disposable)
        }

        bottomSheetDialog.show()
    }

    private fun setupPricing(v: View, direction: Direction?) {
        v.text_price.text = direction?.distance?.calculatePricing()
        v.text_distance.text = direction?.distance?.calculateDistanceKm()

        direction?.let {
            normalControlImpl.toReadyMaps(direction)
        }
    }
}