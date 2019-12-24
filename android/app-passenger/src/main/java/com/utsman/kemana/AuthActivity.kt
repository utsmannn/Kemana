package com.utsman.kemana

import android.os.Bundle
import com.utsman.feature.base.RxAppCompatActivity
import com.utsman.feature.base.intentTo
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        btn_enter.setOnClickListener {
            intentTo(MainActivity::class.java)
        }
    }
}