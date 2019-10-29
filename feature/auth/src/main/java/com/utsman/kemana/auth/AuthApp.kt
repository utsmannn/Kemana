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