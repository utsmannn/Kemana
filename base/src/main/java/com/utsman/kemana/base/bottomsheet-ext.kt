package com.utsman.kemana.base

import android.os.Handler
import com.google.android.material.bottomsheet.BottomSheetBehavior

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