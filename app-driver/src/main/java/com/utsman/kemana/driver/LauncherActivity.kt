/*
 * Copyright 2019 Muhammad Utsman
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
import com.utsman.kemana.base.ext.ProgressHelper
import com.utsman.kemana.base.ext.intentTo
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.base.ext.preferences
import com.utsman.kemana.base.ext.toast
import com.utsman.kemana.base.ext.withPermission

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
                    preferences.edit().putString("user-id", user.userId).apply()

                    logi("user id is --> ${user.userId}")

                    preferencesUser.edit().putString("model", user.userToString()).apply()

                    progressHelper.hideProgressDialog()
                    intentTo(MapsActivity::class.java, bundleOf("user" to user.userToString()))
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
