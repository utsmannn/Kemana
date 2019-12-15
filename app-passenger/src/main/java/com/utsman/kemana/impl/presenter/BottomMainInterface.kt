package com.utsman.kemana.impl.presenter

import com.utsman.kemana.remote.place.Places
import io.reactivex.disposables.Disposable

interface BottomMainInterface {
    fun onSearchStartLocation(list: (List<Places?>?) -> Unit): Disposable
    fun onSearchDestLocation(list: (List<Places?>?) -> Unit): Disposable
    fun onClickOrder(startPlaces: Places, destPlaces: Places)
}