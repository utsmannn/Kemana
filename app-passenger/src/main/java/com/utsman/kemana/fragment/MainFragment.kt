@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.fragment.bottom_sheet.PickupBottomSheet
import com.utsman.kemana.impl.view.FragmentListener
import com.utsman.kemana.impl.view.ILocationView
import com.utsman.kemana.impl.view.IMapView
import com.utsman.kemana.impl.view.IMessagingView
import com.utsman.kemana.maps_callback.PickupMaps
import com.utsman.kemana.maps_callback.ReadyMaps
import com.utsman.kemana.maps_callback.StartMaps
import com.utsman.kemana.presenter.LocationPresenter
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.driver.Passenger
import com.utsman.kemana.remote.driver.Position
import com.utsman.kemana.remote.driver.RemotePresenter
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses
import com.utsman.kemana.remote.toJSONObject
import com.utsman.kemana.remote.toOrderData
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.json.JSONObject

class MainFragment(private val passenger: Passenger?, private val fragmentListener: FragmentListener) : RxFragment(),
    ILocationView, IMapView, IMessagingView {

    private var mainBottomSheetFragment: MainBottomSheet? = null
    private var pickupBottomSheetFragment: PickupBottomSheet? = null

    private lateinit var bottomSheet: BottomSheetUnDrag<View>

    private var mapsPresenter: MapsPresenter? = null
    private var messagingPresenter: MessagingPresenter? = null
    private var remotePresenter: RemotePresenter? = null
    //private var locationPresenter: LocationPresenter? = null

    private lateinit var mapView: MapView
    private lateinit var startMaps: StartMaps
    private lateinit var readyMaps: ReadyMaps
    private lateinit var pickupMaps: PickupMaps

    private var latLng = LatLng()
    private var startLatLng = LatLng()
    private var destLatLng = LatLng()

    private var trackingDisposable: Disposable? = null
    private var cameraDisposable: Disposable? = null

    private val locationPresenter by lazy {
        LocationPresenter(context!!)
    }

    private val bottomDialog by lazy {
        val bottomDialog = BottomSheetDialog(context!!)
        val bottomDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_finding_order, null)
        bottomDialog.setContentView(bottomDialogView)
        bottomDialog.setCancelable(false)

        return@lazy bottomDialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        mapView = v?.mapbox_view!!
        mapView.onCreate(savedInstanceState)
        bottomSheet = BottomSheetBehavior.from(v.main_bottom_sheet) as BottomSheetUnDrag<View>
        bottomSheet.setAllowUserDragging(false)
        bottomSheet.hidden()

        locationPresenter.initLocation(this)

        return v
    }

    private fun revertAllNull() {
        messagingPresenter = null
        remotePresenter = null
    }

    override fun onLocationReady(latLng: LatLng) {
        this.latLng = latLng
        logi("location ready --> $latLng")

        mapsPresenter = MapsPresenter(this)
        messagingPresenter = MessagingPresenter(this)

        remotePresenter = RemotePresenter(composite)
        passenger?.position = Position(latLng.latitude, latLng.longitude)
        mapsPresenter?.mapStart(latLng)

        if (mapsPresenter != null && messagingPresenter != null) {
            mainBottomSheetFragment = MainBottomSheet(mapsPresenter!!, messagingPresenter!!, latLng)
        }

        mainBottomSheetFragment?.pricingGone()

        replaceFragment(mainBottomSheetFragment, R.id.main_frame_bottom_sheet)

        bottomSheet.collapse()
    }

    override fun mapStart(startLatLng: LatLng) {
        startMaps = StartMaps(composite, context, startLatLng) { map, marker ->
            // map ready from invoke

        }

        mapView.getMapAsync(startMaps)
        startMaps.setPaddingBottom(200)
        composite.add(startMaps)
    }

    override fun mapReady(start: Places, destination: Places, polyline: PolylineResponses?) {
        startLatLng = LatLng(start.geometry!![0]!!, start.geometry!![1]!!)
        destLatLng = LatLng(destination.geometry!![0]!!, destination.geometry!![1]!!)

        logi("poly is --> ${polyline?.geometry}")

        if (polyline == null) {
            toast("failed")

            mapView.getMapAsync(startMaps)
            startMaps.setPaddingBottom(200)

        } else {
            readyMaps = ReadyMaps(context, startLatLng, destLatLng, polyline.geometry) { map ->
                // map ready from invokeW
                mainBottomSheetFragment?.pricingVisible()
            }

            mapView.getMapAsync(readyMaps)
            readyMaps.setPaddingBottom(200)
            composite.add(readyMaps)
        }
    }

    override fun mapPickup(orderData: OrderData) {
        logi("driver id is --> ${orderData.attribute.driver?.id}")
        messagingPresenter?.let {
            pickupBottomSheetFragment = PickupBottomSheet(orderData, it)
        }

        pickupMaps = PickupMaps(context, composite, orderData) { mapbox, marker, camDis ->
            replaceFragment(pickupBottomSheetFragment, R.id.main_frame_bottom_sheet)

            cameraDisposable = camDis
            trackingDisposable = Rabbit.fromUrl(RABBIT_URL).listen { from, body ->
                logi("listen from $from - for traking -> $body")
                val type = body.getInt("type")

                when (type) {
                    Type.TRACKING -> {
                        val data = body.getJSONObject("data")

                        logi("driver: $from location is --> $data")
                        val newLat = data.getDouble("lat")
                        val newLon = data.getDouble("lon")
                        val newLatLong = LatLng(newLat, newLon)

                        marker?.moveMarkerSmoothly(newLatLong)
                    }
                }
            }

            composite.addAll(cameraDisposable, trackingDisposable)
        }

        mapView.getMapAsync(pickupMaps)
        composite.add(pickupMaps)
    }

    override fun failedServerConnection() {
        val bottomDialog = BottomSheetDialog(context!!)
        bottomDialog.setContentView(R.layout.bottom_dialog_error)
        bottomDialog.show()
    }

    override fun dispose() {
        readyMaps.dispose()
        startMaps.dispose()
        pickupMaps.dispose()
    }

    override fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        bottomDialog.show()

        remotePresenter?.getDriversActiveEmail {  emails ->
            if (!emails.isNullOrEmpty()) {

                val size = emails.size-1
                var target = 0

                if (target <= size) {
                    finder(emails[target], startPlaces, destPlaces, polyline)
                } else {
                    bottomDialog.dismiss()
                }

                // listen callback driver
                Rabbit.fromUrl(RABBIT_URL).listen { from, body ->
                    logi("from $from is $body")

                    val type = body.getInt("type")
                    val data = body.getJSONObject("data")

                    when (type) {
                        Type.ORDER_CONFIRM -> {
                            val orderData = data.toOrderData()
                            if (orderData.accepted) {
                                //toast("you can get driver")
                                // driver accepted
                                logi("driver id is --> ${orderData.attribute.driver?.id}")
                                setupOrderAccepted(orderData)

                            } else {
                                logi("order rejected")

                                // if target < size, call again with increase target
                                if (target < size) {
                                    logi("target is $target and size is $size")
                                    // increase target with +1 and call again
                                    target = (+1).apply {
                                        finder(emails[this], startPlaces, destPlaces, polyline)
                                    }
                                } else {
                                    toast("cannot driver accepted, please try again")
                                    bottomDialog.cancel()
                                }
                            }
                        }
                    }
                }

            } else {
                toast("driver not found")
                bottomDialog.dismiss()
            }
        }
    }

    override fun orderCancel() {
        fragmentListener.onDetachMainFragment()
    }

    private fun setupOrderAccepted(orderData: OrderData) {
        logi("order accepted")
        bottomDialog.cancel()

        val driver = orderData.attribute.driver
        logi("driver id is --> ${driver?.id}")
        mapsPresenter?.mapOrder(orderData)
    }

    private fun finder(email: String, startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        val jsonBody = JSONObject()
        jsonBody.apply {
            put("person", passenger?.toJSONObject())
            put("start", startPlaces.placeName)
            put("destination", destPlaces.placeName)
            put("startPlace", startPlaces.toJSONObject())
            put("destPlace", destPlaces.toJSONObject())
            put("distance", polyline.distance)
        }

        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("type", Type.ORDER_REQUEST)
            put("data", jsonBody)
        }

        logi("start send to $email")
        Rabbit.fromUrl(RABBIT_URL).publishTo(email, true, jsonRequest) {
            bottomDialog.dismiss()
            toast("try again -> ${it.localizedMessage}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}