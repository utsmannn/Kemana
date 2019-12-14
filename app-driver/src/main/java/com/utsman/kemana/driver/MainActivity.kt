@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.driver

import android.Manifest
import android.annotation.SuppressLint
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
import com.utsman.kemana.base.*
import com.utsman.kemana.driver.fragment.MainFragment
import com.utsman.kemana.driver.services.LocationServices
import com.utsman.kemana.remote.driver.*
import isfaaghyth.app.notify.Notify

class MainActivity : RxAppCompatActivity() {

    private var driver: Driver? = null
    private lateinit var locationServices: Intent
    private lateinit var mainFragment: MainFragment

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)

        driver = getBundleFrom("driver")
        locationServices = Intent(this, LocationServices::class.java)
        mainFragment = MainFragment(driver)
        //mainFragment = MainFragment.withDriver(driver)

        setupPermission {
            startService(locationServices)

            composite.delay(900) {
                driver?.let {
                    Notify.send(it)
                }
            }
        }

        replaceFragment(mainFragment, R.id.main_frame)
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

        Handler().postDelayed({
            stopService(locationServices)
        }, 800)
    }
}