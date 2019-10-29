package com.utsman.kemana.auth

interface AuthListener {
    fun onLoaded()
    fun onLoginSuccess(user: User, password: String, fromRegister: Boolean)
    fun onLoginUnauthorized()
    fun onLoginFailed(throwable: Throwable)
    fun toRegister()
}