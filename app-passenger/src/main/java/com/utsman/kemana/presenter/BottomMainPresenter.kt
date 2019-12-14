package com.utsman.kemana.presenter

import com.utsman.kemana.impl.presenter.BottomMainInterface
import com.utsman.kemana.impl.view.IBottomMainView
import com.utsman.kemana.remote.place.Places
import io.reactivex.disposables.Disposable

class BottomMainPresenter(private val iBottomMainView: IBottomMainView) : BottomMainInterface {


    override fun onSearchStartLocation(query: String): Disposable {
        return iBottomMainView.onSearchStartLocation(query)
    }

    override fun onSearchDestLocation(query: String): Disposable {
        return iBottomMainView.onSearchDestLocation(query)
    }

    override fun onClickOrder(startPlaces: Places, destPlaces: Places) {
        iBottomMainView.onClickOrder(startPlaces, destPlaces)
    }
}