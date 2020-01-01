package com.utsman.kemana.driver.state

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.utsman.feature.remote.model.Direction
import com.utsman.kemana.driver.impl.BaseRenderMapsView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StateMapsViewPresenter(private val mainStateMapsView: MainStateMapsView) {
    private val composite = CompositeDisposable()
    private var normalMaps: BaseRenderMapsView? = null
    private var pickupMaps: BaseRenderMapsView? = null
    private var orderMaps: BaseRenderMapsView? = null

    fun renderMapsNormal(mapboxMap: MapboxMap, style: Style) {
        val observable =  Observable.just(mapboxMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                removeAll(style)
            }
            .subscribe({
                normalMaps = mainStateMapsView.mapsNormal(mapboxMap, style)
            }, {
                it.printStackTrace()
            })

        composite.add(observable)
    }

    fun renderMapsPickup(mapboxMap: MapboxMap, style: Style, direction: Direction) {
        val observable =  Observable.just(mapboxMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                removeAll(style)
            }
            .subscribe({
                pickupMaps = mainStateMapsView.mapsPickup(mapboxMap, style, direction)
            }, {
                it.printStackTrace()
            })

        composite.add(observable)
    }

    fun renderMapsOrder(mapboxMap: MapboxMap, style: Style) {
        val observable =  Observable.just(mapboxMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                removeAll(style)
            }
            .subscribe({
                orderMaps = mainStateMapsView.mapsOrder(mapboxMap, style)
            }, {
                it.printStackTrace()
            })

        composite.add(observable)
    }

    fun dispose() {
        composite.dispose()
    }

    private fun removeAll(style: Style) {
        normalMaps?.remove(style)
        pickupMaps?.remove(style)
        orderMaps?.remove(style)
    }
}