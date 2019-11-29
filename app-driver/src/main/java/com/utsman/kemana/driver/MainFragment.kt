package com.utsman.kemana.driver

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.base.NotifyState
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.timer
import com.utsman.kemana.driver.impl.IMapView
import com.utsman.kemana.driver.presenter.MapsPresenter
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : RxFragment(), IMapView {

    private lateinit var mapsPresenter: MapsPresenter
    private lateinit var mapbox: MapboxMap
    private var marker: Marker? = null
    private var newLatLng = LatLng()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
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
        view?.mapbox_view?.getMapAsync { mapbox ->
            this.mapbox = mapbox
            mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
                val markerOption = MarkerOptions.Builder()
                    .addIcon(R.drawable.mapbox_marker_icon_default)
                    .addPosition(latLng)
                    .setId("me")
                    .build(context!!)

                marker = mapbox.addMarker(markerOption).get("me")

                mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0))

                compositeDisposable.timer(5000) {
                    mapbox.animateCamera(CameraUpdateFactory.newLatLng(newLatLng))
                }
            }
        }
    }

    private fun updateLocationActive() {
        Notify.send(NotifyState(NotifyState.UPDATE_LOCATION))
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        marker?.moveMarkerSmoothly(newLatLng)
        this.newLatLng = newLatLng
    }

}