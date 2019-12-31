@file:Suppress("FunctionName")

package com.utsman.feature.base

import android.content.Context
import java.util.*

fun Context.Preferences_saveEmail(email: String?) = apply {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    pref.edit().putString("email", email).apply()
}

fun Context.Preferences_getEmail(): String {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    return pref.getString("email", "not define email") ?: ""
}

fun Context.getId(): String {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    val email = pref.getString("email", "not define email") ?: ""
    return UUID.nameUUIDFromBytes(email.toByteArray()).toString()
}