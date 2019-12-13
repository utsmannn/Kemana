package com.utsman.kemana.presenter

import com.utsman.kemana.impl.IMessagingView
import com.utsman.kemana.impl.MessagingInterface

class MessagingPresenter(private val iMessagingView: IMessagingView) : MessagingInterface {
    override fun findDriver() {
        iMessagingView.findDriver()
    }

    override fun retrieveDriver() {
        iMessagingView.retrieveDriver()
    }
}