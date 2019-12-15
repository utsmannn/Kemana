package com.utsman.kemana.presenter

import com.utsman.kemana.impl.presenter.BottomMainInterface
import com.utsman.kemana.impl.view.IBottomMainView
import com.utsman.kemana.remote.place.Places
import io.reactivex.disposables.Disposable

class BottomMainPresenter(private val iBottomMainView: IBottomMainView) : BottomMainInterface {


    override fun onSearchStartLocation(list: (List<Places?>?) -> Unit): Disposable {
        return iBottomMainView.onSearchStartLocation(list)
    }

    override fun onSearchDestLocation(list: (List<Places?>?) -> Unit): Disposable {
        return iBottomMainView.onSearchDestLocation(list)
    }

    override fun onClickOrder(startPlaces: Places, destPlaces: Places) {
        iBottomMainView.onClickOrder(startPlaces, destPlaces)
    }
}