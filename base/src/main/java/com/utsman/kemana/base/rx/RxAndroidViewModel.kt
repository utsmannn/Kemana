package com.utsman.kemana.base.rx

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

open class RxAndroidViewModel(application: Application) : AndroidViewModel(application) {
    open val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}