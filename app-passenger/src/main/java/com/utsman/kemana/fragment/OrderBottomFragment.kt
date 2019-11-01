package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.R
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.base.ext.calculateDistanceKm
import com.utsman.kemana.base.ext.calculatePricing
import com.utsman.kemana.base.rx.RxFragment
import com.utsman.kemana.fragment.callback.CallbackFragment
import com.utsman.kemana.fragment.callback.CallbackFragmentOrder
import com.utsman.kemana.maputil.toLocation
import com.utsman.kemana.places.PlaceRouteApp
import kotlinx.android.synthetic.main.fragment_sheet_order.*

class OrderBottomFragment(private val callbackFragment: CallbackFragment,
                          private val callbackFragmentOrder: CallbackFragmentOrder) : RxFragment() {

    private lateinit var placeRouteApp: PlaceRouteApp
    private var fromLatLng: LatLng = LatLng()
    private var toLatLng: LatLng = LatLng()
    private var distance = 0.0
    private var position = 0

    fun setFromLatLng(fromLatLng: LatLng) {
        this.fromLatLng = fromLatLng
    }

    fun setToLatLng(toLatLng: LatLng) {
        this.toLatLng = toLatLng
    }

    fun setDistance(distance: Double) {
        this.distance = distance
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sheet_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeRouteApp = PlaceRouteApp(compositeDisposable)
        callbackFragment.onCollapse()

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

        val distanceKm = distance.calculateDistanceKm()
        val priceRp = distance.calculatePricing()

        text_distance.text = distanceKm
        text_pricing.text = priceRp

        btn_order_cancel.setOnClickListener {
            callbackFragmentOrder.onBtnBackPress()
        }

        btn_order.setOnClickListener {
            startOrder(position)
        }
    }

    fun startOrder(position: Int) {
        this.position = position
        val backendlessApp = BackendlessApp(activity!!.application, compositeDisposable)

        backendlessApp.getDriversList().observe(context as LifecycleOwner, Observer { users ->
            callbackFragmentOrder.onBtnOrderPress(users, position, distance)
        })
    }
}