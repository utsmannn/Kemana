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
import com.utsman.feature.base.logi
import com.utsman.feature.remote.instance.CheckInstance
import com.utsman.feature.remote.instance.RequestServiceInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : RxAppCompatActivity() {

    private val checkInstance by lazy {
        CheckInstance.create()
    }

    private val requestServiceInstance by lazy {
        RequestServiceInstance.create()
    }

    private val email = "sample@mail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setupPermission {
            btn_enter.setOnClickListener {
                //intentTo(MainActivity::class.java)

                val observable = checkInstance.checkService(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        if (it.data.contains(email)) {
                            intentTo(MainActivity::class.java)
                        }
                    }
                    .doOnError {
                        requestService(email)
                    }
                    .subscribe({

                    }, {

                    })

                composite.add(observable)
            }
        }
    }

    private fun requestService(mail: String) {
        val observable = requestServiceInstance.requestToServer(mail)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi(it.status)
            }, {
                it.printStackTrace()
            })

        composite.add(observable)
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