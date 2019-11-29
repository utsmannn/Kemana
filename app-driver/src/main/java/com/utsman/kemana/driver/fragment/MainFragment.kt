package com.utsman.kemana.driver.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.utsman.kemana.base.NotifyState
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.timer
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.impl.IMapView
import com.utsman.kemana.driver.maps_callback.MainMaps
import com.utsman.kemana.driver.presenter.MapsPresenter
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import com.utsman.smartmarker.mapbox.Marker
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : RxFragment(), IMapView {

    private lateinit var mapView: MapView
    private lateinit var mapsPresenter: MapsPresenter
    private lateinit var mapbox: MapboxMap
    private lateinit var mainMaps: MainMaps

    private var marker: Marker? = null
    private var newLatLng = LatLng()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mapView = view?.mapbox_view!!
        mapsPresenter = MapsPresenter(this)

        val subs = Notify.listen(LocationSubs::class.java, NotifyProvider(), Consumer { locationSubs ->
            logi("NOTIFY --> receiving location from service")
            mapsPresenter.initMap(locationSubs)
        })

        val updateSubs = Notify.listen(UpdateLocationSubs::class.java, NotifyProvider(), Consumer { updateLocationSubs ->
            logi("NOTIFY --> receiving location update from service")
            mapsPresenter.startUpdate(updateLocationSubs)
        })

        Handler().postDelayed({
            updateLocationActive()
        }, 2000)

        compositeDisposable.addAll(subs, updateSubs)
        return view
    }

    override fun onLocationReady(latLng: LatLng) {
        this.newLatLng = latLng

        mainMaps = MainMaps(context, latLng) { map, marker ->
            this.mapbox = map
            this.marker = marker

            compositeDisposable.timer(5000) {
                mapbox.animateCamera(CameraUpdateFactory.newLatLng(newLatLng))
            }
        }

        mapView.getMapAsync(mainMaps)
    }

    private fun updateLocationActive() {
        Notify.send(NotifyState(NotifyState.UPDATE_LOCATION))
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        this.newLatLng = newLatLng
        marker?.moveMarkerSmoothly(newLatLng)
    }

}