package com.utsman.kemana.driver

import android.Manifest
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.utsman.kemana.auth.AuthApp
import com.utsman.kemana.auth.AuthListener
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.UserLogin
import com.utsman.kemana.auth.userToString
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.ProgressHelper
import com.utsman.kemana.base.intentTo
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.preferences
import com.utsman.kemana.base.toast
import com.utsman.kemana.base.withPermission

class LauncherActivity : AppCompatActivity() {

    private val appId = Key.APP_ID
    private val restId = Key.REST_KEY
    private lateinit var authApp: AuthApp
    private lateinit var progressHelper: ProgressHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        authApp = AuthApp(appId, restId)
        progressHelper = ProgressHelper(this)

        withPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            loginStart()
        }
    }

    private fun loginStart() {
        Handler().postDelayed({
            val preferences = preferences("account")
            val preferencesUser = preferences("user")
            val login = preferences.getString("email", "mail") ?: "mail"
            val password = preferences.getString("password", "pass") ?: "pass"
            val userLogin = UserLogin(login, password)

            authApp.login(userLogin, object : AuthListener {
                override fun toRegister() {

                }

                override fun onLoaded() {
                    progressHelper.showProgressDialog()
                }

                override fun onLoginSuccess(user: User, password: String, fromRegister: Boolean) {
                    logi("token is --> ${user.token}")
                    preferences.edit().putString("token", user.token).apply()
                    preferencesUser.edit().putString("model", user.userToString()).apply()

                    progressHelper.hideProgressDialog()
                    intentTo(MapActivity::class.java, bundleOf("user" to user.userToString()))
                    finish()
                }

                override fun onLoginUnauthorized() {
                    progressHelper.hideProgressDialog()
                    toast("login unautorized")
                    intentTo(AuthActivity::class.java)
                    finish()
                }

                override fun onLoginFailed(throwable: Throwable) {
                    progressHelper.hideProgressDialog()
                    toast("login failed --> ${throwable.message}")
                    throwable.printStackTrace()
                }
            }, password)

        }, 3000)
    }
}
