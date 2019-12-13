package com.utsman.kemana.presenter

import com.utsman.kemana.impl.view.IMessagingView
import com.utsman.kemana.impl.presenter.MessagingInterface
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

class MessagingPresenter(private val iMessagingView: IMessagingView) :
    MessagingInterface {

    override fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        iMessagingView.findDriver(startPlaces, destPlaces, polyline)
    }
}