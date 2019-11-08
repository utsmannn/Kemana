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

package com.utsman.kemana.auth

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthApp(private val appId: String, private val restKey: String) {

    private val instance = AuthInstance.create()

    fun register(user: User, loginListener: AuthListener, password: String) {
        instance.register(appId, restKey, user)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val u = response.body()
                    u?.let { usr ->
                        loginListener.onLoginSuccess(usr, password, true)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    loginListener.onLoginFailed(t)
                }
            })
    }

    fun login(userLogin: UserLogin, loginListener: AuthListener, password: String) {
        instance.login(appId, restKey, userLogin)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val u = response.body()
                    if (response.code() != 401) {
                        u?.let { usr ->
                            loginListener.onLoginSuccess(usr, password, false)
                        }
                    } else {
                        loginListener.onLoginUnauthorized()
                    }
                    Log.i("anjay", "onResponse: " + response.code())
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    loginListener.onLoginFailed(t)
                }
            })
    }
}