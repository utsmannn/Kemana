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