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

package com.utsman.kemana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.utsman.kemana.auth.AuthListener
import com.utsman.kemana.auth.User
import com.utsman.kemana.auth.adapter.AuthPagerAdapter
import com.utsman.kemana.auth.fragment.LoginFragment
import com.utsman.kemana.auth.fragment.RegisterFragment
import com.utsman.kemana.auth.userToString
import com.utsman.kemana.base.ext.ProgressHelper
import com.utsman.kemana.base.ext.intentTo
import com.utsman.kemana.base.ext.logi
import com.utsman.kemana.base.ext.preferences
import com.utsman.kemana.base.ext.toast
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var progressHelper: ProgressHelper

    private val authListener = object : AuthListener {
        override fun toRegister() {
            auth_view_pager.setCurrentItem(1, true)
        }

        override fun onLoaded() {
            progressHelper.showProgressDialog()
        }

        override fun onLoginSuccess(user: User, password: String, fromRegister: Boolean) {
            val preferences = preferences("account")
            logi("${user.email} -- ${user.password}")
            logi("token is --> ${user.token}")
            preferences.edit().apply {
                putString("email", user.email)
                putString("password", password)
                putString("token", user.token)
            }.apply()

            progressHelper.hideProgressDialog()

            //intentTo(MapActivity::class.java, bundleOf("user" to user.userToString()))
            intentTo(MapsActivity::class.java, bundleOf("user" to user.userToString()))
            finish()
        }

        override fun onLoginUnauthorized() {
            progressHelper.hideProgressDialog()
            toast("login unautorized")
            auth_view_pager.setCurrentItem(1, true)
        }

        override fun onLoginFailed(throwable: Throwable) {
            progressHelper.hideProgressDialog()
            toast("login failed --> ${throwable.message}")
            throwable.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        progressHelper = ProgressHelper(this)

        val loginFragment = LoginFragment(authListener)
        val registerFragment = RegisterFragment(authListener, false)

        val authPagerAdapter = AuthPagerAdapter(supportFragmentManager)
        authPagerAdapter.addFragment(loginFragment, registerFragment)

        auth_view_pager.adapter = authPagerAdapter
    }
}