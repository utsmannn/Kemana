@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.utsman.kemana.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun Context.toast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

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

fun CompositeDisposable.timer(interval: Long, action: () -> Unit) {

    val subs = Observable.interval(interval, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe ({
            action.invoke()
        }, {
            loge(it.localizedMessage)
            it.printStackTrace()
        })

    add(subs)
}