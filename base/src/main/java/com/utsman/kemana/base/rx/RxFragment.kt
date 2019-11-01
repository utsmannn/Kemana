package com.utsman.kemana.base.rx

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class RxFragment : Fragment() {
    open val compositeDisposable = CompositeDisposable()
}