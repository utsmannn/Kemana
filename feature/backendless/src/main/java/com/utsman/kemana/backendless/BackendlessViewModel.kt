/*
 * Copyright 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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