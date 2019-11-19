package com.utsman.kemana.base.ext

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun CompositeDisposable.addTimer(second: Long, func: () -> Unit) {
    add(
        Observable.interval(second, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                func.invoke()
            }
    )
}