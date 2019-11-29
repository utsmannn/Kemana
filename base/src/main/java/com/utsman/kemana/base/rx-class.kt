package com.utsman.kemana.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

interface BaseRx {
    val compositeDisposable: CompositeDisposable
        get() = CompositeDisposable()
}

open class RxAppCompatActivity : AppCompatActivity(), BaseRx {

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}

open class RxFragment : Fragment(), BaseRx {

    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
    }
}

open class RxService : Service(), BaseRx {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

}