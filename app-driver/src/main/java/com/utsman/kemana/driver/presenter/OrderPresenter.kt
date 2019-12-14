package com.utsman.kemana.driver.presenter

import com.utsman.kemana.driver.impl.presenter.OrderInterface
import com.utsman.kemana.driver.impl.view.IOrderView
import com.utsman.kemana.remote.place.Places

class OrderPresenter(private val iOrderView: IOrderView) : OrderInterface {
    override fun onPickup(places: Places?) {
        iOrderView.onPickup(places)
    }

    override fun onTake(places: Places?) {
        iOrderView.onTake(places)
    }

    override fun onArrive(places: Places?) {
        iOrderView.onArrive(places)
    }
}