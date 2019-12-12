package com.utsman.kemana

import com.utsman.kemana.base.RxService
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection

class MessagingServices : RxService() {

    override fun onCreate() {
        super.onCreate()

        /*val rmqaConnection = RmqaConnection.Builder(this)
            .setServer("localhost:5672")
            .setUsername("user1")
            .setPassword("1234")
            .setVhost("user1")*/
    }
}