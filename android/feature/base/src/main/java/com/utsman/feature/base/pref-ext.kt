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

fun Context.Preferences_saveId(id: String?) = apply {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    pref.edit().putString("id", id).apply()
}

fun Context.Preferences_getId(): String {
    val pref = getSharedPreferences("account", Context.MODE_PRIVATE)
    return pref.getString("id", "not define id") ?: ""
}