package com.utsman.kemana.backendless

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.utsman.kemana.auth.User
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.rx.RxAndroidViewModel
import com.utsman.kemana.base.ext.loge
import com.utsman.kemana.base.ext.logi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BackendlessViewModel(application: Application) : RxAndroidViewModel(application) {

    private val instance = BackendlessInstance.create()
    private val contentType = "application/json"
    private val liveListUser = MutableLiveData<List<User>>()

    /*fun getDriversList(onSuccess: (drivers: List<User>?) -> Unit, onError: (errorMsg: String?) -> Unit) {
        val obs = instance.getDriverList(Key.APP_ID, Key.REST_KEY, "driver_active")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("sukses check driver list")

                onSuccess(it)
            }, {
                loge("error get driver list -> ${it.message}")
                onError(it.message)
            })

        disposable.add(obs)
    }*/

    fun getDriversList(): LiveData<List<User>> {
        val obs = instance.getDriverList(Key.APP_ID, Key.REST_KEY, "driver_active")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("sukses check driver list --> ${it?.size}")
                liveListUser.postValue(it)
            }, {
                loge("error get driver list -> ${it.message}")
            })

        disposable.add(obs)

        return liveListUser
    }
}