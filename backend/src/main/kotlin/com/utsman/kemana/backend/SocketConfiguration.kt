package com.utsman.kemana.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import java.net.ServerSocket

class SocketConfiguration private constructor(private val server: SocketIOServer?): SocketInstance {

    override fun getSocketServer(action: (SocketIOServer?) -> Unit) {
        action.invoke(server)
    }

    companion object {
        private var server: SocketIOServer? = null

        fun init() {
            val config = Configuration()
            config.hostname = "localhost"
            config.port = 8080

            server = SocketIOServer(config)
            println("socket run on ${config.port}")
        }

        fun getConfig(): SocketConfiguration {
            return SocketConfiguration(server)
        }
    }

}