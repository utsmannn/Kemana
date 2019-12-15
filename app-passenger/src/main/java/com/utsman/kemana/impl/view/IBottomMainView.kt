package com.utsman.kemana.impl.view

import com.utsman.kemana.remote.place.Places
import io.reactivex.disposables.Disposable

interface IBottomMainView {
    fun onSearchStartLocation(list: (List<Places?>?) -> Unit): Disposable
    fun onSearchDestLocation(list: (List<Places?>?) -> Unit): Disposable
    fun onClickOrder(startPlaces: Places, destPlaces: Places)
}