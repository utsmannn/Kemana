package com.utsman.kemana.driver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.utsman.easygooglelogin.EasyGoogleLogin
import com.utsman.easygooglelogin.LoginResultListener
import com.utsman.kemana.base.intentTo
import com.utsman.kemana.base.logi
import kotlinx.android.synthetic.main.component_login_screen.*

class AuthActivity : AppCompatActivity(), LoginResultListener {

    private lateinit var googleLogin: EasyGoogleLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_login_screen)
        googleLogin = EasyGoogleLogin(this)

        val token = getString(R.string.default_web_client_id)
        googleLogin.initGoogleLogin(token, this)

        btn_google_sign.setOnClickListener {
            googleLogin.signIn(this)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        googleLogin.initOnStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleLogin.onActivityResult(this, requestCode, data)
    }

    override fun onLoginSuccess(user: FirebaseUser?) {
        logi("login success")
        intentTo(MainActivity::class.java)
    }

    override fun onLogoutSuccess(task: Task<Void>?) {
    }

    override fun onLoginFailed(exception: Exception?) {
    }

    override fun onLogoutError(exception: Exception?) {
    }
}