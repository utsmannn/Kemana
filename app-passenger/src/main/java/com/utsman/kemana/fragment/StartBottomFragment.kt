package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.R
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.hideKeyboard
import com.utsman.kemana.base.loge
import com.utsman.kemana.fragment.callback.CallbackFragment
import com.utsman.kemana.fragment.callback.CallbackFragmentStart
import com.utsman.kemana.maputil.calculateBound
import com.utsman.kemana.maputil.toLocation
import com.utsman.kemana.places.Feature
import com.utsman.kemana.places.PlaceRouteApp
import com.utsman.kemana.toLatLng
import com.utsman.recycling.setupAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sheet_start.*
import kotlinx.android.synthetic.main.item_place.view.*
import java.util.concurrent.TimeUnit

class StartBottomFragment(private val callbackFragment: CallbackFragment,
                          private val callbackFragmentStart: CallbackFragmentStart) : RxFragment() {

    private lateinit var placeRouteApp: PlaceRouteApp
    private var currentLatLng = LatLng()

    fun setCurrentLatLng(currentLatLng: LatLng) {
        this.currentLatLng = currentLatLng
        callbackFragmentStart.fromLatLng(currentLatLng)
    }

    fun clearFocus() {
        input_to_location.clearFocus()
        input_from_location.clearFocus()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sheet_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackFragment.onCollapse()
        placeRouteApp = PlaceRouteApp(compositeDisposable)

        input_from_location.setupSearch(true)
        input_to_location.setupSearch(false)

        placeRouteApp.getMyAddress(currentLatLng.toLocation())
            .observe(context as LifecycleOwner, Observer {
                val addressName = it.place_name
                input_from_location.setText(addressName)
            })

    }

    private fun EditText.setupSearch(fromMyLocation: Boolean) {

        // force focusable
        setOnTouchListener { view, motionEvent ->
            callbackFragment.onExpand()
            selectAll()
            false
        }

        rxTextWatcher(2) { q ->
            val bounds = calculateBound(currentLatLng, 50f, 50f)

            val bbox = "${bounds.southWest.longitude}," +
                "${bounds.southWest.latitude}," +
                "${bounds.northEast.longitude}," +
                "${bounds.northEast.latitude}"

            rv_list_place.setupAdapter<Feature>(R.layout.item_place) { adapter, context, list ->
                bind { itemView, position, item ->
                    itemView.text_place_name.text = item?.place_name
                    itemView.text_address.text = item?.properties?.address

                    itemView.setOnClickListener {
                        setText(item?.place_name)
                        activity?.hideKeyboard()

                        item?.geometry?.let {
                            if (fromMyLocation) {
                                callbackFragmentStart.fromLatLng(it.toLatLng())
                            } else {
                                callbackFragmentStart.toLatLng(it.toLatLng())
                            }
                        }

                        callbackFragment.onHidden()
                        callbackFragment.onOrder(true)
                    }
                }

                addLoader(R.layout.item_loader) {
                    idLoader = R.id.progress_circular
                    idTextError = R.id.error_text_view
                }

                if (hasFocus()) {
                    placeRouteApp.getPlaces(q, bbox)
                        .observe(context as LifecycleOwner, Observer { features ->
                            submitList(features)
                        })

                    placeRouteApp.getNetworkState()
                        .observe(context as LifecycleOwner, Observer { network ->
                            list?.clear()
                            adapter.notifyDataSetChanged()
                            submitNetworkState(network)
                        })
                }

                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        list?.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun EditText.rxTextWatcher(delay: Long, query: (string: String) -> Unit) {
        val obs = afterTextChangeEvents()
            .subscribeOn(Schedulers.io())
            .map { it.editable.toString() }
            .filter { it.length > 2 }
            .debounce(delay, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ q ->
                query.invoke(q)
            }, { tw ->
                loge(tw.localizedMessage)
            })

        compositeDisposable.addAll(obs)
    }
}