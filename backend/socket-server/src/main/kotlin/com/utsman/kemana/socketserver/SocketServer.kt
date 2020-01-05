package com.utsman.kemana.socketserver

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class SocketServer {

    @Throws(InterruptedException::class)
    fun start() {
        val config = Configuration()
        config.hostname = "localhost"
        config.port = 1212

        val server = SocketIOServer(config)
        val listClient: MutableList<String> = mutableListOf()

        server.addEventListener("name", String::class.java) { client, data, ackSender ->
            /*listClient.find { it.clientId == client?.sessionId.toString() }?.apply {
                name = data
                println("$name is connected")
            }



            server.broadcastOperations.sendEvent("test", data)*/

            listClient.forEachIndexed { index, s ->

            }
        }

        server.addDisconnectListener { client ->
            /*listClient.find { it.clientId == client.sessionId.toString() }?.apply {
                println("$name is disconnect")
            }*/


        }

        server.addConnectListener {
            /*val clientId = it.sessionId
            val socketObject = SocketObject(clientId = clientId.toString())
            listClient.add(socketObject)*/
        }

        println("running at port -> ${server.configuration.port}")
        server.start()
    }
}