@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.impl.ILocationView
import com.utsman.kemana.impl.IMapView
import com.utsman.kemana.maps_callback.ReadyMaps
import com.utsman.kemana.maps_callback.StartMaps
import com.utsman.kemana.presenter.LocationPresenter
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses
import com.utsman.kemana.subscriber.LocationSubs
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : RxFragment(), ILocationView, IMapView {

    private lateinit var mainBottomSheetFragment: MainBottomSheet
    private lateinit var bottomSheet: BottomSheetUnDrag<View>
    private lateinit var mapsPresenter: MapsPresenter

    private lateinit var mapView: MapView
    private lateinit var startMaps: StartMaps
    private lateinit var readyMaps: ReadyMaps

    private lateinit var locationPresenter: LocationPresenter
    private var latLng = LatLng()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        mapView = v?.mapbox_view!!

        locationPresenter = LocationPresenter(context!!)
        locationPresenter.initLocation(this)

        mapsPresenter = MapsPresenter(this)

        mainBottomSheetFragment = MainBottomSheet(mapsPresenter)

        bottomSheet = BottomSheetBehavior.from(v.main_bottom_sheet) as BottomSheetUnDrag<View>
        bottomSheet.setAllowUserDragging(false)
        bottomSheet.hidden()

        replaceFragment(mainBottomSheetFragment, R.id.main_frame_bottom_sheet)

        bottomSheet.collapse()

        return v
    }

    override fun onLocationReady(latLng: LatLng) {
        this.latLng = latLng
        mapStart(latLng)
    }

    override fun getNowLocation() {

    }

    override fun mapStart(startLatLng: LatLng) {
        startMaps = StartMaps(compositeDisposable, context, startLatLng) { map, marker ->
            // map ready from invoke
        }

        Notify.send(LocationSubs(startLatLng))

        mapView.getMapAsync(startMaps)
        startMaps.setPaddingBottom(200)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        val startLatLng = LatLng(start.geometry!![0]!!, start.geometry!![1]!!)
        val destinationLatLng = LatLng(destination.geometry!![0]!!, destination.geometry!![1]!!)

        logi("poly is --> ${polyline?.geometry}")

        if (polyline == null) {
            toast("failed")
            mapView.getMapAsync(startMaps)
            startMaps.setPaddingBottom(200)
            mainBottomSheetFragment.pricingGone()
        } else {
            readyMaps = ReadyMaps(context, startLatLng, destinationLatLng, polyline.geometry) { map ->
                // map ready from invoke
            }

            mapView.getMapAsync(readyMaps)
            readyMaps.setPaddingBottom(300)
            mainBottomSheetFragment.pricingVisible()
        }


    }

    override fun mapOrder() {

    }


}