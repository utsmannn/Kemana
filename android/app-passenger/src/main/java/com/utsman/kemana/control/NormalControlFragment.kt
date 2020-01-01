package com.utsman.kemana.control

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.feature.base.*
import com.utsman.feature.remote.instance.DirectionInstance
import com.utsman.feature.remote.instance.PlaceInstance
import com.utsman.feature.remote.model.Direction
import com.utsman.feature.remote.model.Place
import com.utsman.feature.remote.model.orderData
import com.utsman.kemana.R
import com.utsman.kemana.impl.NormalControlImpl
import com.utsman.recycling.extentions.NetworkState
import com.utsman.recycling.setupAdapter
import com.utsman.smartmarker.location.LocationWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.bottoh_sheet_location_picker.view.*
import kotlinx.android.synthetic.main.control_normal.view.*
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
    private var direction: Direction? = null

    private val email by lazy {
        context?.Preferences_getEmail()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.control_normal, container, false)
        hidePricing(v)
        locationWatcher.getLocation { location ->
            val from = listOf(location.latitude, location.longitude)
            val observablePlace = placeInstance.getCurrentPlace(email, from, HERE_API_KEY)
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

                    directionInstance.getDirection(email, fromPlace?.geometry, toPlace?.geometry, MAPBOX_TOKEN)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            this.direction = it
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

                    directionInstance.getDirection(email, fromPlace?.geometry, toPlace?.geometry, MAPBOX_TOKEN)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            this.direction = it
                            setupPricing(v, it)
                        }, {
                            it.printStackTrace()
                        })
                }
            }
        }

        v.btn_order.setOnClickListener {
            val order = orderData {
                // data class Order(
                //    var id: String? = null,
                //    var time: String? = null,
                //    @SerializedName("driver_id")
                //    var driverId: String? = null,
                //    @SerializedName("passenger_id")
                //    var passengerId: String? = null,
                //    val from: Place? = null,
                //    val to: Place? = null,
                //    val distance: Double? = null
                //)

                //passengerId = UUID.nameUUIDFromBytes()
                this.from = fromPlace
                this.to = toPlace
                this.distance = direction?.distance
            }
        }

        return v
    }

    private fun showLocationPicker(placeName: (Place?, LatLng) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val viewDialog =
            LayoutInflater.from(context).inflate(R.layout.bottoh_sheet_location_picker, null)
        bottomSheetDialog.setContentView(viewDialog)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = bottomSheetDialog.window
            window?.let {
                var flags: Int = window.decorView.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = flags
                window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorBg)
            }
        }

        bottomSheetDialog.setOnShowListener {

            val bottomSheetFrame = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheet = BottomSheetBehavior.from(bottomSheetFrame)

            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheet.isHideable = false
            bottomSheet.isFitToContents = true
            bottomSheet.skipCollapsed = true

            // disable drag
            (bottomSheetFrame?.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
        }

        viewDialog?.rv_location?.visibility = View.GONE

        viewDialog?.input_location?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewDialog.rv_location.visibility = View.VISIBLE
                viewDialog.main_container.visibility = View.GONE
            } else {
                viewDialog.rv_location.visibility = View.GONE
                viewDialog.main_container.visibility = View.VISIBLE
            }
        }

        viewDialog.maps_view_picker.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.OUTDOORS) { style ->
                val initLat = fromPlace?.geometry?.get(0) ?: 0.0
                val initLon = fromPlace?.geometry?.get(1) ?: 0.0
                val initLatLon = LatLng(initLat, initLon)
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLatLon, 16.0))

                mapboxMap.addOnMapClickListener {
                    viewDialog?.input_location?.clearFocus()
                    return@addOnMapClickListener true
                }

                mapboxMap.addOnCameraMoveListener {
                    val latLonPicker = mapboxMap.cameraPosition.target
                    placeInstance.getCurrentPlace(email, listOf(latLonPicker.latitude, latLonPicker.longitude), HERE_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .map { it.places[0] }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            viewDialog.text_address_result.text = it.placeName
                        }, {
                            it.printStackTrace()
                        })
                }

                viewDialog.btn_location_picker_set.setOnClickListener {
                    val latLonPicker = mapboxMap.cameraPosition.target
                    placeInstance.getCurrentPlace(email, listOf(latLonPicker.latitude, latLonPicker.longitude), HERE_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .map { it.places.get(0) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            placeName.invoke(it, latLonPicker)
                            bottomSheetDialog.dismiss()
                        }, {
                            it.printStackTrace()
                        })
                }

                viewDialog?.rv_location?.setupAdapter<Place>(R.layout.item_location) { adapter, context, list ->
                    list?.clear()
                    adapter.notifyDataSetChanged()
                    bind { itemView, position, item ->
                        itemView.item_text_place_title.text = item?.placeName
                        itemView.item_text_place_address.text = item?.addressName

                        itemView.setOnClickListener {
                            val latLng = LatLng(item!!.geometry!![0], item.geometry!![1])

                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0))
                            viewDialog.main_container.visibility = View.VISIBLE

                            viewDialog.input_location?.clearFocus()
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
                            placeInstance.searchPlace(email, query, fromPlace?.geometry, HERE_API_KEY)
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
            }
        }

        bottomSheetDialog.show()
    }

    private fun setupPricing(v: View, direction: Direction?) {
        v.detail_pricing_container.visibility = View.VISIBLE

        v.text_price.text = direction?.distance?.calculatePricing()
        v.text_distance.text = direction?.distance?.calculateDistanceKm()

        direction?.let {
            normalControlImpl.toReadyMaps(direction)
        }
    }

    private fun hidePricing(v: View) {
        v.detail_pricing_container.visibility = View.GONE
    }
}