package com.utsman.kemana.base

import android.os.Handler
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<View>.expand() {
    isHideable = true
    state = BottomSheetBehavior.STATE_EXPANDED

    Handler().postDelayed({
        isHideable = false
    }, 500)
}

fun BottomSheetBehavior<View>.hidden() {
    isHideable = true
    state = BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<View>.isExpand(): Boolean {
    return state == BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<View>.isCollapse(): Boolean {
    return state == BottomSheetBehavior.STATE_COLLAPSED
}