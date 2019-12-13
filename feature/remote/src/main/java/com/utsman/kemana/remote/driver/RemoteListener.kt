package com.utsman.kemana.remote.driver

interface RemoteListener {
    fun insertDriver(driverItem: Driver, driver: (success: Boolean, driver: Driver?) -> Unit)
    fun getDriversActive(list: (List<Driver>?) -> Unit)
    fun getDriversActiveEmail(email: (List<String>?) -> Unit)
    fun getDriver(id: String, driver: (Driver?) -> Unit)
    fun getDriver(id: String) : Driver?
    fun editDriver(id: String, position: Position, driver: (Driver?) -> Unit)
    fun editDriverByEmail(email: String, position: Position, driver: (Driver?) -> Unit)
    fun deleteDriver(id: String, status: (Boolean?) -> Unit)
    fun deleteDriverByEmail(email: String, status: (Boolean?) -> Unit)

    fun registerDriver(driverItem: Driver, driver: (success: Boolean, driver: Driver?) -> Unit)
    fun checkRegisteredDriver(email: String?, hasRegister: (Boolean?) -> Unit)
    fun getRegisteredDriverById(id: String, driver: (Driver?) -> Unit)
    fun getAttrRegisteredDriver(id: String, attr: (Attribute?) -> Unit)
}