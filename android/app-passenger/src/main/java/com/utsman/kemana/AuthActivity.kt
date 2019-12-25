package com.utsman.kemana

import android.Manifest
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.utsman.feature.base.RxAppCompatActivity
import com.utsman.feature.base.intentTo
import com.utsman.feature.base.loge
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setupPermission {
            btn_enter.setOnClickListener {
                intentTo(MainActivity::class.java)
            }
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