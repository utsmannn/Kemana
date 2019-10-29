package com.utsman.kemana.base

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

// Resources r = getResources();
// int px = Math.round(TypedValue.applyDimension(
//     TypedValue.COMPLEX_UNIT_DIP, 14,r.getDisplayMetrics()));

fun Context.dpToInt(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}