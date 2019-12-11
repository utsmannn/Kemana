package com.utsman.kemana.remote

import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RemotePresenter(private val disposable: CompositeDisposable) : RemoteListener {
    private val remoteInstance = RemoteInstance.create()

    override fun insertDriver(driverItem: Driver, driver: (success: Boolean, driver: Driver?) -> Unit) {
        val action = remoteInstance.insertDriver(driverItem)
            .subscribeOn(Schedulers.io())
            .map { it.data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    driver.invoke(true, it[0])
                    logi("driver is ${it[0]}")
                }
            }, {
                it.printThrow("insert driver")
                driver.invoke(false,null)
            })

        disposable.add(action)
    }

    override fun getDriversActive(list: (List<Driver>?) -> Unit) {
        val action = remoteInstance.getAllDriver()
            .subscribeOn(Schedulers.io())
            .map { it.data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("driver list success")
                list.invoke(it)
            }, {
                it.printThrow("driver list")
            })

        disposable.add(action)
    }

    override fun getDriver(id: String, driver: (Driver?) -> Unit) {
        val action = remoteInstance.getDriver(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val list = it.data
                if (!list.isNullOrEmpty()) {
                    driver.invoke(list[0])
                } else {
                    driver.invoke(null)
                }

            }, {
                it.printThrow("get driver")
            })

        disposable.add(action)
    }

    override fun getDriver(id: String): Driver? {

        var driver: Driver? = null

        val action = remoteInstance.getDriver(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val list = it.data
                driver = if (!list.isNullOrEmpty()) {
                    list[0]
                } else {
                    null
                }

            }, {
                it.printThrow("get driver")
            })

        disposable.add(action)

        return driver
    }

    override fun editDriver(id: String, position: Position, driver: (Driver?) -> Unit) {
        val action = remoteInstance.editDriver(id, position)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val list = it.data
                if (!list.isNullOrEmpty()) {
                    driver.invoke(list[0])
                } else {
                    driver.invoke(null)
                }

            }, {
                it.printThrow("get driver edit")
            })

        disposable.add(action)
    }

    override fun editDriverByEmail(email: String, position: Position, driver: (Driver?) -> Unit) {
        val action = remoteInstance.editDriverByEmail(email, position)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val list = it.data
                if (!list.isNullOrEmpty()) {
                    driver.invoke(list[0])
                } else {
                    driver.invoke(null)
                }

            }, {
                it.printThrow("get driver edit")
            })

        disposable.add(action)
    }

    override fun deleteDriver(id: String, status: (Boolean?) -> Unit) {
        val action = remoteInstance.deleteDriver(id)
            .subscribeOn(Schedulers.io())
            .map { it.message }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == "OK") status.invoke(true)
                else status.invoke(null)
            }, {
                status.invoke(false)
                it.printThrow("delete fail")
            })

        disposable.add(action)
    }

    override fun deleteDriverByEmail(email: String, status: (Boolean?) -> Unit) {
        val action = remoteInstance.deleteDriverByEmail(email)
            .subscribeOn(Schedulers.io())
            .map { it.message }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == "OK") status.invoke(true)
                else status.invoke(null)
            }, {
                status.invoke(false)
                it.printThrow("delete fail")
            })

        disposable.add(action)
    }

}