package com.utsman.kemana.driver.presenter

import com.utsman.kemana.driver.impl.presenter.ActiveStateInterface
import com.utsman.kemana.driver.impl.view_state.IActiveState

class ActivatedStatePresenter(private val iActiveState: IActiveState) : ActiveStateInterface {
    private var activated = false

    override fun activeState() {
        activated = true
        iActiveState.activeState()
    }

    override fun deactivateState() {
        activated = false
        iActiveState.deactivateState()
    }

    override fun setState(state: Boolean) {
        activated = state
    }

    override fun getState(): Boolean {
        return activated
    }
}