package com.utsman.kemana.auth

import com.google.gson.Gson

fun String.stringToUser() = Gson().fromJson(this, User::class.java)
fun User.userToString() = Gson().toJson(this)