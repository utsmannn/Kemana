@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.fragment.MainFragment
import com.utsman.kemana.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.impl.view.FragmentListener
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.remote.driver.Passenger
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : RxAppCompatActivity(), FragmentListener {

    private lateinit var mainFragment: MainFragment
    private val person by lazy {
        getBundleFrom<Passenger>("passenger")
    }

    @SuppressLint("AuthLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)


        mainFragment = MainFragment(person, this)

        setupPermission {
            replaceFragment(mainFragment, R.id.main_frame)
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

    override fun onDetachMainFragment() {
        //restartFragment(mainFragment, R.id.main_frame)
        //intentTo(MainActivity::class.java, bundleOf("passenger" to person))
        finish()

        composite.delay(500) {
            val bundle = bundleOf("passenger" to person)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}
