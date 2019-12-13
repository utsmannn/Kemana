package com.utsman.kemana.presenter

import com.utsman.kemana.impl.IMessagingView
import com.utsman.kemana.impl.MessagingInterface
import com.utsman.kemana.remote.place.Places

class MessagingPresenter(private val iMessagingView: IMessagingView) : MessagingInterface {

    override fun findDriver(startPlaces: Places, destPlaces: Places) {
        iMessagingView.findDriver(startPlaces, destPlaces)
    }

    override fun retrieveDriver() {
        iMessagingView.retrieveDriver()
    }
}