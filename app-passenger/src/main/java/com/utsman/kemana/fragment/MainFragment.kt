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
import com.utsman.kemana.impl.ILocationView
import com.utsman.kemana.maps_callback.MainMaps
import com.utsman.kemana.presenter.LocationPresenter
import com.utsman.smartmarker.mapbox.Marker
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : RxFragment(), ILocationView {

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
        mainMaps = MainMaps(context, latLng) { map, marker ->
            this.mapbox = map
            this.marker = marker

            compositeDisposable.timer(5000) {
                mapbox.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }

        mapView.getMapAsync(mainMaps)
    }

    override fun getNowLocation() {

    }
}