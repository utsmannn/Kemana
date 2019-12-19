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

@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.utsman.kemana.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import java.util.concurrent.TimeUnit
import android.os.Handler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Context.toast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String?) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()



fun logi(msg: String?) = Log.i("KEMANA-INFO", msg)
fun loge(msg: String?) = Log.e("KEMANA-ERROR", msg)

fun View.gone() = apply { visibility = View.GONE }

fun View.visible() = apply { visibility = View.VISIBLE }

fun View.isGone() = visibility == View.GONE
fun View.isVisible() = visibility == View.VISIBLE

fun ImageView.loadCircleUrl(url: String?) = Glide.with(this).load(url).circleCrop().into(this)

fun Activity.intentTo(c: Class<*>, bundle: Bundle? = null)  {

    val intent = Intent(this, c).apply {
        if (bundle != null)
            putExtras(bundle)
    }
    startActivity(intent)
}

fun Fragment.intentTo(c: Class<*>, bundle: Bundle? = null)  {

    val intent = Intent(context, c).apply {
        if (bundle != null)
            putExtras(bundle)
    }
    startActivity(intent)
}

fun <T: Parcelable>Activity.getBundleFrom(key: String): T? = intent.extras?.getParcelable(key)

fun timer(interval: Long, action: () -> Unit): Disposable {

    return Observable.interval(interval, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe ({
            action.invoke()
        }, {
            loge(it.localizedMessage)
            it.printStackTrace()
        })
}

/*fun Notify.listenNotifyState(state: (Int) -> Unit) {
    listen(NotifyState::class.java, NotifyProvider(), Consumer { value ->
        logi("notify receiving")
        state.invoke(value.state)
    }, Consumer {
        loge(it.localizedMessage)
        it.printStackTrace()
    })
}*/

fun BottomSheetBehavior<*>.expand() {
    isHideable = true
    state = BottomSheetBehavior.STATE_EXPANDED

    Handler().postDelayed({
        isHideable = false
    }, 500)
}

fun BottomSheetBehavior<*>.hidden() {
    isHideable = true
    state = BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<*>.collapse() {
    isHideable = true
    state = BottomSheetBehavior.STATE_COLLAPSED
    Handler().postDelayed({
        isHideable = false
    }, 500)
}

fun BottomSheetBehavior<*>.isExpand(): Boolean {
    return state == BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<*>.isCollapse(): Boolean {
    return state == BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<*>.isHidden(): Boolean {
    return state == BottomSheetBehavior.STATE_HIDDEN
}

fun CompositeDisposable.delay(long: Long, action: () -> Unit) {
    val disposable = Observable.just(long)
        .subscribeOn(Schedulers.io())
        .delay(long, TimeUnit.MILLISECONDS)
        .map {
            return@map action
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            it.invoke()
        }

    add(disposable)
}