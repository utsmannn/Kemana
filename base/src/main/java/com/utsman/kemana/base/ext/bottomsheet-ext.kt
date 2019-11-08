/*
 * Copyright 2019 Muhammad Utsman
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

package com.utsman.kemana.base.ext

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

fun BottomSheetBehavior<*>.isHidden(): Boolean {
    return state == BottomSheetBehavior.STATE_HIDDEN
}