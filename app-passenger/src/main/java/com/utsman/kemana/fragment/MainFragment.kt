package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.utsman.kemana.R
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.timer
import com.utsman.kemana.base.toast
import com.utsman.kemana.impl.ILocationView
import com.utsman.kemana.impl.IMapView
import com.utsman.kemana.maps_callback.MainMaps
import com.utsman.kemana.presenter.LocationPresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.subscriber.LocationSubs
import com.utsman.smartmarker.mapbox.Marker
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : RxFragment(), ILocationView, IMapView {

    private lateinit var mapView: MapView
    private lateinit var mapbox: MapboxMap
    private lateinit var mainMaps: MainMaps

    private lateinit var locationPresenter: LocationPresenter

    private var marker: Marker? = null
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

        return v
    }

    override fun onLocationReady(latLng: LatLng) {
        this.latLng = latLng
        mapStart(latLng)
    }

    override fun getNowLocation() {

    }

    override fun mapStart(startLatLng: LatLng) {
        mainMaps = MainMaps(compositeDisposable, context, startLatLng) { map, marker ->
            this.mapbox = map
            this.marker = marker

            compositeDisposable.timer(5000) {
                mapbox.animateCamera(CameraUpdateFactory.newLatLng(startLatLng))
            }
        }

        Notify.send(LocationSubs(startLatLng))

        mapView.getMapAsync(mainMaps)
    }

    override fun mapReady(start: Places, destination: Places) {
    }

    override fun mapOrder() {

    }


}