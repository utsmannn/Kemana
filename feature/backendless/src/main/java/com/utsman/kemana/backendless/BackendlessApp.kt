package com.utsman.kemana.backendless

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.utsman.kemana.auth.User
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class BackendlessApp(private val application: Application, private val disposable: CompositeDisposable) {

    private val instance = BackendlessInstance.create()
    private val contentType = "application/json"

    fun saveUserToType(token: String, table: String, user: User, onSuccess: (user: User) -> Unit, onError: ((Throwable?) -> Unit)? = null) {
        val obs = instance.saveUserToTable(contentType, token, Key.APP_ID, Key.REST_KEY, table, user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("tii")
                onSuccess.invoke(it)
            }, {
                loge("aaaaa")
                onError?.invoke(it)
            })

        disposable.add(obs)
    }

    fun updateDriverLocation(table: String, objId: String, user: User, token: String, onSuccess: (User) -> Unit, onError: ((Throwable?) -> Unit)? = null) {
        val obs = instance.updateDriveLocation(
            contentType = contentType,
            token = token,
            appId = Key.APP_ID,
            restKey = Key.REST_KEY,
            table = table,
            objectId = objId,
            user = user
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ u ->
                onSuccess.invoke(u)
            }, { e ->
                onError?.invoke(e)
            })

        disposable.add(obs)
    }

    fun getUserById(objId: String, table: String): LiveData<User> {
        val liveUser = MutableLiveData<User>()
        val obs = instance.getDriverById(Key.APP_ID, Key.REST_KEY, objId, table)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                liveUser.postValue(it)
            }

        disposable.add(obs)

        return liveUser
    }

    fun deleteDriverActive(objectId: String, token: String, table: String, onSuccess: (JSONObject) -> Unit, onError: ((Throwable) -> Unit)? = null) {
        val obs = instance.deleteDriveActive(
            contentType = contentType,
            token = token,
            appId = Key.APP_ID,
            restKey = Key.REST_KEY,
            objectId = objectId,
            table = table
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ u ->
                onSuccess.invoke(u)
            }, { e ->
                onError?.invoke(e)
            })

        disposable.add(obs)
    }

    fun getDriversList(onSuccess: (drivers: List<User>?) -> Unit, onError: (errorMsg: String?) -> Unit) {
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
    }
}