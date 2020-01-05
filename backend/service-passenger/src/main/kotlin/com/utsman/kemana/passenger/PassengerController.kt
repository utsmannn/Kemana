package com.utsman.kemana.passenger

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/passenger")
class PassengerController {

    @Autowired
    lateinit var environment: Environment

    private lateinit var server: SocketIOServer

    @RequestMapping("/connect", method = [RequestMethod.GET])
    fun request(): Responses {

        val config = Configuration()
        config.hostname = "localhost"
        config.port = 0
        println("try plug socket port")
        server = SocketIOServer(config)
        server.addEventListener("name", String::class.java) { client, data, ackSender ->

        }

        println("start running socket")
        server.start()

        return Responses("ok", server.configuration.port)
    }

}