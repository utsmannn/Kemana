package com.utsman.kemana.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

fun Context.toast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun logi(msg: String?) = Log.i("anjay-info", msg)
fun loge(msg: String?) = Log.e("anjay-error", msg)

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

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

fun Activity.withPermission(permission: String, listener: Context?.() -> Unit) {
    Dexter.withActivity(this)
        .withPermission(permission)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                listener(this@withPermission)
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                toast("Permission denied, application will be close on 3 second")
                Handler().postDelayed({
                    finish()
                }, 3000)
            }

        })
        .check()
}

fun BottomSheetBehavior<View>.collapse() {
    isHideable = true
    state = BottomSheetBehavior.STATE_COLLAPSED
    Handler().postDelayed({
        isHideable = false
    }, 500)
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}

fun Activity.getBundleFrom(key: String): Any? = intent.extras?.getParcelable(key)

fun Context.preferences(key: String): SharedPreferences =
    this.getSharedPreferences(key, Context.MODE_PRIVATE)

@SuppressLint("DefaultLocale")
fun String?.formatId() = this?.toLowerCase()?.replace(" ", "")

object SavedUserKey {

    object Status {
        val driverActive = "is_active"
    }

    const val driverKey = "driver-detail"
    const val passengerKey = "passenger-detail"

    const val name = "name"
    const val id = "id"
    const val photoUrl = "photo_url"
    const val vehcType = "vehicles_type"
    const val vehcNum = "vehicles_num"
    const val latitude = "lat"
    const val longitude = "lon"
}

fun Activity?.hideKeyboard() {
    this?.let {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity?.showKeyboard() {
    this?.let {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}