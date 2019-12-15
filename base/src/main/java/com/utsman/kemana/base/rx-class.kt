package com.utsman.kemana.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

interface BaseRx {
    val composite: CompositeDisposable
        get() = CompositeDisposable()
}

open class RxAppCompatActivity : AppCompatActivity(), BaseRx {

    override fun onDestroy() {
        composite.dispose()
        super.onDestroy()
    }
}

open class RxFragment : Fragment(), BaseRx {

    override fun onDestroyView() {
        composite.dispose()
        super.onDestroyView()
    }
}

open class RxService : Service(), BaseRx {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        composite.dispose()
        super.onDestroy()
    }

}

open class BaseDisposableCompletable : DisposableCompletableObserver() {
    override fun onComplete() {
        logi("complete")
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }
}