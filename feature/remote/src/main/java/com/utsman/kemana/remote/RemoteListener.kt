package com.utsman.kemana.remote

import androidx.lifecycle.LiveData

interface RemoteListener {
    fun insertDriver(driverItem: Driver, driver: (Driver?) -> Unit)
    fun getDriversActive(list: (List<Driver>?) -> Unit)
    fun getDriver(id: String, driver: (Driver?) -> Unit)
    fun getDriver(id: String) : Driver?
    fun editDriver(id: String, position: Position, driver: (Driver?) -> Unit)
    fun deleteDriver(id: String, status: (Boolean?) -> Unit)
    fun detachPresenter()
}