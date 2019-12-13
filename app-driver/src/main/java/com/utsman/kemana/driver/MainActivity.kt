@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.driver

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.driver.fragment.MainFragment
import com.utsman.kemana.driver.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.driver.impl.view_state.IActiveState
import com.utsman.kemana.driver.services.LocationServices
import com.utsman.kemana.driver.subscriber.ObjectOrderSubs
import com.utsman.kemana.driver.subscriber.ReadyOrderSubs
import com.utsman.kemana.remote.driver.*
import com.utsman.kemana.remote.toJSONObject
import com.utsman.kemana.remote.toPassenger
import com.utsman.kemana.remote.toPlace
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.bottom_dialog_receiving_order.view.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.json.JSONObject

class MainActivity : RxAppCompatActivity(), IActiveState {

    private lateinit var bottomSheet: BottomSheetUnDrag<View>
    private lateinit var mainFragment: MainFragment
    private lateinit var mainBottomSheetFragment: MainBottomSheet
    private lateinit var locationServices: Intent
    private var driver: Driver? = null

    private var onPassengerOrder = true

    private val bottomDialogView by lazy {
        LayoutInflater.from(this).inflate(R.layout.bottom_dialog_receiving_order, null)
    }

    private val bottomDialog by lazy {
        val bottomDialog = BottomSheetDialog(this)
        bottomDialog.setContentView(bottomDialogView)
        bottomDialog.setCancelable(false)

        return@lazy bottomDialog
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)
        locationServices = Intent(this, LocationServices::class.java)

        bottomSheet = BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetUnDrag<View>
        bottomSheet.setAllowUserDragging(false)
        bottomSheet.hidden()

        mainFragment = MainFragment()
        mainBottomSheetFragment = MainBottomSheet(this)
        driver = getBundleFrom("driver")

        setupPermission {
            startService(locationServices)

            Handler().postDelayed({
                driver?.let {
                    Notify.send(it)
                }
            }, 900)
        }

        replaceFragment(mainFragment, R.id.main_frame)
        replaceFragment(mainBottomSheetFragment, R.id.main_frame_bottom_sheet)


        Notify.listenNotifyState { state ->
            when (state) {
                NotifyState.READY -> {
                    bottomSheet.collapse()
                }
            }
        }

        Notify.listen(ReadyOrderSubs::class.java, NotifyProvider(), Consumer { ready ->
            onPassengerOrder = ready.onOrder
        })

        Notify.listen(ObjectOrderSubs::class.java, NotifyProvider(), Consumer {
            val obj = it.jsonObject

            val passenger = obj.getJSONObject("person").toPassenger()
            val startPlace = obj.getJSONObject("startPlace").toPlace()
            val destPlace = obj.getJSONObject("destPlace").toPlace()
            val startName = obj.getString("start")
            val destName = obj.getString("destination")
            val distance = obj.getDouble("distance")

            val textPassengerName = bottomDialogView.text_name_passenger
            val textPricing = bottomDialogView.text_price
            val textDistance = bottomDialogView.text_distance
            val textFrom = bottomDialogView.text_from
            val textDest = bottomDialogView.text_to
            val btnAccept = bottomDialogView.btn_accept
            val btnReject = bottomDialogView.btn_reject

            textPassengerName.text = passenger.name
            textPricing.text = distance.calculatePricing()
            textDistance.text = distance.calculateDistanceKm()
            textFrom.text = startPlace.placeName
            textDest.text = destPlace.placeName

            btnAccept.setOnClickListener {
                callbackToPassenger(true, passenger)
            }

            btnReject.setOnClickListener {
                callbackToPassenger(false, passenger)
            }

            showBottomDialog()
        })
    }

    private fun callbackToPassenger(accepted: Boolean, passenger: Passenger) {
        val orderDataAttr = OrderDataAttr(
            orderID = System.currentTimeMillis().toString(),
            driver = driver,
            passenger = passenger
        )
        val orderData = OrderData(
            accepted = accepted,
            attribute = orderDataAttr
        )

        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("type", Type.ORDER_CONFIRM)
            put("data", orderData.toJSONObject())
        }

        if (onPassengerOrder) {
            Rabbit.fromUrl(RABBIT_URL)
                .publishTo(passenger.email!!, false, jsonRequest)
                .apply {
                    dismissBottomDialog()
                }
        } else {
            toast("order canceled from passenger")
            dismissBottomDialog()
        }
    }

    override fun activeState() {
        logi("state --> driver active")
        Notify.send(NotifyState(RemoteState.INSERT_DRIVER))
    }

    override fun deactivateState() {
        logi("state --> drive deactive")
        Notify.send(NotifyState(RemoteState.DELETE_DRIVER))
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

    override fun onDestroy() {
        super.onDestroy()
        deactivateState()

        Handler().postDelayed({
            stopService(locationServices)
        }, 800)
    }

    private fun dismissBottomDialog() {
        if (bottomDialog.isShowing) {
            bottomDialog.dismiss()
        }
    }

    private fun showBottomDialog() {
        if (!bottomDialog.isShowing) {
            bottomDialog.show()
        }
    }
}