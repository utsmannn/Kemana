/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.utsman.featurerabbitmq.Rabbit
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