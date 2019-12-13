@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.impl.ILocationView
import com.utsman.kemana.impl.IMapView
import com.utsman.kemana.impl.IMessagingView
import com.utsman.kemana.maps_callback.ReadyMaps
import com.utsman.kemana.maps_callback.StartMaps
import com.utsman.kemana.presenter.LocationPresenter
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.driver.Passenger
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses
import com.utsman.kemana.remote.toJSONObject
import com.utsman.kemana.subscriber.LocationSubs
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.json.JSONObject

class MainFragment(private val passenger: Passenger?) : RxFragment(), ILocationView, IMapView, IMessagingView {

    private lateinit var mainBottomSheetFragment: MainBottomSheet
    private lateinit var bottomSheet: BottomSheetUnDrag<View>
    private lateinit var mapsPresenter: MapsPresenter
    private lateinit var messagingPresenter: MessagingPresenter
    private lateinit var remotePresenter: RemotePresenter

    private lateinit var mapView: MapView
    private lateinit var startMaps: StartMaps
    private lateinit var readyMaps: ReadyMaps

    private lateinit var locationPresenter: LocationPresenter
    private var latLng = LatLng()

    private var startLatLng = LatLng()
    private var destLatLng = LatLng()

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
        messagingPresenter = MessagingPresenter(this)

        remotePresenter = RemotePresenter(composite)

        mainBottomSheetFragment = MainBottomSheet(mapsPresenter, messagingPresenter)

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
        startMaps = StartMaps(composite, context, startLatLng) { map, marker ->
            // map ready from invoke
        }

        Notify.send(LocationSubs(startLatLng))

        mapView.getMapAsync(startMaps)
        startMaps.setPaddingBottom(200)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        startLatLng = LatLng(start.geometry!![0]!!, start.geometry!![1]!!)
        destLatLng = LatLng(destination.geometry!![0]!!, destination.geometry!![1]!!)

        logi("poly is --> ${polyline?.geometry}")

        if (polyline == null) {
            toast("failed")
            mapView.getMapAsync(startMaps)
            startMaps.setPaddingBottom(200)
            mainBottomSheetFragment.pricingGone()
        } else {
            readyMaps = ReadyMaps(context, startLatLng, destLatLng, polyline.geometry) { map ->
                // map ready from invokeW
            }

            mapView.getMapAsync(readyMaps)
            readyMaps.setPaddingBottom(300)
            mainBottomSheetFragment.pricingVisible()
        }
    }

    override fun mapOrder() {

    }

    override fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        toast("start finding")

        val dialogBuilder = AlertDialog.Builder(context!!)
        dialogBuilder.setView(R.layout.dialog_finding_order)
        dialogBuilder.setCancelable(false)

        val dialog = dialogBuilder.create()

        dialog.show()

        remotePresenter.getDriversActiveEmail {  emails ->
            if (!emails.isNullOrEmpty()) {

                val size = emails.size
                var target = 0

                if (target <= size) {
                    finder(emails[target], startPlaces, destPlaces, polyline)
                } else {
                    dialog.dismiss()
                }

                // listen callback driver
                Rabbit.fromUrl(RABBIT_URL).listen { from, body ->

                    // if reject from driver
                    target =+ 1
                    finder(emails[target], startPlaces, destPlaces, polyline)
                }

            } else {
                toast("driver not found")
                dialog.dismiss()
            }
        }
    }

    override fun retrieveDriver() {

    }

    private fun finder(email: String, startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("person", passenger?.toJSONObject())
            put("start", startPlaces.placeName)
            put("destination", destPlaces.placeName)
            put("startPlace", startPlaces.toJSONObject())
            put("destPlace", destPlaces.toJSONObject())
            put("distance", polyline.distance)
        }

        Rabbit.fromUrl(RABBIT_URL).publishTo(email, jsonRequest)
    }


}