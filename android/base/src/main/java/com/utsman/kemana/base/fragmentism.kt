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

package com.utsman.kemana.base

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment?, frameId: Int){
    fragment?.let {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment?, frameId: Int) {
    fragment?.let {
        supportFragmentManager.inTransaction{replace(frameId, fragment)}
    }
}

fun Fragment.replaceFragment(fragment: Fragment?, frameId: Int) {
    fragment?.let {
        childFragmentManager.inTransaction{replace(frameId, fragment)}
    }
}

fun AppCompatActivity.detachFragment(fragment: Fragment?) {
    fragment?.let {
        supportFragmentManager.inTransaction { remove(fragment) }
    }
}

fun AppCompatActivity.restartFragment(fragment: Fragment?, frameId: Int) {
    fragment?.let {
        supportFragmentManager.inTransaction { remove(fragment) }

        Handler().postDelayed({
            replaceFragment(fragment, frameId)
        }, 500)
    }
}