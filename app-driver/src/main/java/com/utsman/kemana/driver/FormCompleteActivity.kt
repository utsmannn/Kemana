package com.utsman.kemana.driver

import android.os.Bundle
import androidx.core.os.bundleOf
import com.utsman.kemana.base.*
import com.utsman.kemana.remote.driver.Attribute
import com.utsman.kemana.remote.driver.Driver
import com.utsman.kemana.remote.driver.RemotePresenter
import kotlinx.android.synthetic.main.activity_completion_form.*

class FormCompleteActivity : RxAppCompatActivity() {

    private lateinit var remotePresenter: RemotePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completion_form)

        remotePresenter = RemotePresenter(composite)

        val driver = getBundleFrom<Driver>("driver")

        btn_save.setOnClickListener {
            val vehiclesType = input_vehicles_type.text.toString()
            val vehiclesNumber = input_vehicles_number.text.toString()

            if (vehiclesType != "" && vehiclesNumber != "") {

                val attr = Attribute(vehiclesType, vehiclesNumber)
                driver?.attribute = attr

                btn_save.isEnabled = false
                remotePresenter.registerDriver(driver!!) { status, driver ->
                    if (status) {
                        logi("saved success")
                        intentTo(MainActivity::class.java, bundleOf("driver" to driver))
                        finish()
                    } else {
                        btn_save.isEnabled = true
                    }
                }

            } else {
                toast("Salah satu tidak boleh kosong")
            }
        }
    }
}