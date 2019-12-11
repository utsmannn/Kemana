@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.utsman.kemana.fragment.MainFragment
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.presenter.MapsPresenter
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment
    private lateinit var mainBottomSheetFragment: MainBottomSheet
    private lateinit var bottomSheet: BottomSheetUnDrag<View>
    private lateinit var mapsPresenter: MapsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment()
        mapsPresenter = MapsPresenter(mainFragment)

        mainBottomSheetFragment = MainBottomSheet(mapsPresenter)

        bottomSheet = BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetUnDrag<View>
        bottomSheet.setAllowUserDragging(false)
        bottomSheet.hidden()

        setupPermission {
            replaceFragment(mainFragment, R.id.main_frame)
            replaceFragment(mainBottomSheetFragment, R.id.main_frame_bottom_sheet)

            bottomSheet.collapse()
        }

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
