package com.utsman.kemana.remote.place

import com.utsman.kemana.base.logi
import com.utsman.kemana.remote.printThrow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlacePresenter(private val disposable: CompositeDisposable) : PlaceListener {
    private val placeInterface = PlaceInterface.create()

    override fun search(query: String, from: String, places: (List<Places?>?) -> Unit) {
        val action = placeInterface.search(query, from)
            .subscribeOn(Schedulers.io())
            .map { it.places }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("places result success")
                places.invoke(it)
            }, {
                it.printThrow("search failed")
            })

        disposable.add(action)
    }

    override fun getAddress(from: String, places: (Places?) -> Unit) {
        val action = placeInterface.searchAddress(from)
            .subscribeOn(Schedulers.io())
            .map { it.places?.get(0) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("get places result success")
                places.invoke(it)
            }, {
                it.printThrow("get places failed")
            })

        disposable.add(action)

    }

    override fun getPolyline(from: String, to: String, result: (PolylineResponses?) -> Unit) {
        val action = placeInterface.getPolyline(from, to)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("get direction success")
                result.invoke(it)
            }, {
                result.invoke(null)
                it.printThrow("get direction failed")
            })

        disposable.add(action)
    }
}