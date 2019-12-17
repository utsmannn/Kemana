/*
 * Copyright (c) 2019 Muhammad Utsman
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