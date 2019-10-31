package com.utsman.kemana.fragment.callback

import com.utsman.kemana.auth.User

interface CallbackFragmentOrder {
    fun onBtnOrderPress(listDriver: List<User?>, i: Int, distance: Double)
    fun onBtnBackPress()
}