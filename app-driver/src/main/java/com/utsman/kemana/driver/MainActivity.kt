@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.driver

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.driver.fragment.MainFragment
import com.utsman.kemana.driver.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.driver.impl.view_state.IActiveState
import com.utsman.kemana.driver.services.LocationServices
import com.utsman.kemana.remote.Driver
import com.utsman.kemana.remote.RemoteState
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : RxAppCompatActivity(), IActiveState {

    private lateinit var bottomSheet: BottomSheetUnDrag<View>
    private lateinit var mainFragment: MainFragment
    private lateinit var mainBottomSheetFragment: MainBottomSheet
    private lateinit var locationServices: Intent
    private var driver: Driver? = null

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
    }

    override fun activeState() {
        logi("state --> driver active")
        Notify.send(NotifyState(RemoteState.INSERT_DRIVER))
    }

    override fun deactiveState() {
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
        deactiveState()

        Handler().postDelayed({
            stopService(locationServices)
        },  800)
    }
}