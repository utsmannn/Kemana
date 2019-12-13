package com.utsman.kemana.driver

import android.content.Context
import java.util.*

fun Context.saveEmail(email: String) = apply {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    pref.edit().putString("email", email).apply()
}

fun Context.getEmail(): String {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    return pref.getString("email", "not define email") ?: ""
}

fun Context.getId(): String {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    val email = pref.getString("email", "not define email") ?: ""
    return UUID.nameUUIDFromBytes(email.toByteArray()).toString()
}