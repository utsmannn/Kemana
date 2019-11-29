package com.utsman.kemana.driver

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.kemana.base.*
import com.utsman.kemana.driver.impl.IMapView
import com.utsman.kemana.driver.presenter.MapsPresenter
import com.utsman.kemana.driver.services.LocationServices
import com.utsman.kemana.driver.subscriber.LocationSubs
import com.utsman.kemana.driver.subscriber.UpdateLocationSubs
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity(), IMapView {

    private lateinit var mapsPresenter: MapsPresenter
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)
        mapsPresenter = MapsPresenter(this)

        setupPermission {
            val service = LocationServices()
            val intentService = Intent(this, service.javaClass)
            startService(intentService)

            Handler().postDelayed({
                logi("location update started --> activity")
                Notify.send(NotifyState(NotifyState.UPDATE_LOCATION))
            }, 200)
        }

        val subs = Notify.listen(LocationSubs::class.java, NotifyProvider(), Consumer { locationSubs ->
            logi("NOTIFY --> receiving location from service")
            mapsPresenter.initMap(locationSubs)
        })

        val updateSubs = Notify.listen(UpdateLocationSubs::class.java, NotifyProvider(), Consumer { updateLocationSubs ->
            logi("NOTIFY --> receiving location update from service")
            mapsPresenter.startUpdate(updateLocationSubs)
        })

        compositeDisposable.addAll(subs, updateSubs)
    }

    override fun onLocationReady(latLng: LatLng) {
        mapbox_view.getMapAsync { mapbox ->
            mapbox.setStyle(Style.MAPBOX_STREETS) { style ->

                val markerOption = MarkerOptions.Builder()
                    .addIcon(R.drawable.mapbox_marker_icon_default)
                    .addPosition(latLng)
                    .setId("me")
                    .build(this)

                marker = mapbox.addMarker(markerOption).get("me")

                mapbox.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0))
            }
        }
    }

    override fun onLocationUpdate(newLatLng: LatLng) {
        marker?.moveMarkerSmoothly(newLatLng)
    }

    private fun setupPermission(ready: () -> Unit) {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    ready.invoke()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    loge("permission denied")
                }

            })
            .check()
    }
}