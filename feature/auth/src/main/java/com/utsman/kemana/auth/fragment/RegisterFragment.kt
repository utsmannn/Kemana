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

package com.utsman.kemana.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.utsman.kemana.auth.AuthApp
import com.utsman.kemana.auth.AuthListener
import com.utsman.kemana.auth.R
import com.utsman.kemana.auth.User
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.ext.gone
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment(private val authListener: AuthListener, private val driver: Boolean) : Fragment() {

    private lateinit var authApp: AuthApp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authApp = AuthApp(Key.APP_ID, Key.REST_KEY)

        if (!driver) {
            input_vehicles_plat.gone()
            input_vehicles_type.gone()
        }

        btn_register.setOnClickListener {
            authListener.onLoaded()
            val email = input_email.text.toString()
            val password = input_password.text.toString()
            val name = input_name.text.toString()
            val userId = "$email-${System.currentTimeMillis()}"

            if (driver) {
                val vehiclesType = input_vehicles_type.text.toString()
                val vehiclesNum = input_vehicles_plat.text.toString()

                val user = User(userId, name, email, password, vehiclesType, vehiclesNum)
                authApp.register(user, authListener, password)
            } else {
                val user = User(userId, name, email, password)
                authApp.register(user, authListener, password)
            }
        }

    }
}