package com.utsman.kemana.remote

interface IRemoteView {
    fun getAllDriver()
    fun getDriver(id: String)
    fun editDriver(id: String, position: Position)
    fun deleteDriver(id: String)
}