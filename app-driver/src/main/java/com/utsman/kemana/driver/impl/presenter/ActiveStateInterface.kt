package com.utsman.kemana.driver.impl.presenter

interface ActiveStateInterface {
    fun activeState()
    fun deactivateState()
    fun setState(state: Boolean)
    fun getState(): Boolean
}