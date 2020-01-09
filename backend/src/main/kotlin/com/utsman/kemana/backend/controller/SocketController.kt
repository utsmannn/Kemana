package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.SocketConfiguration
import com.utsman.kemana.backend.model.Responses
import com.utsman.kemana.backend.model.User
import com.utsman.kemana.backend.model.toUser
import org.json.simple.JSONObject
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/socket")
class SocketController {

    private val listUserOnline: MutableList<User> = mutableListOf()

    init {
        println("start socket event")
        SocketConfiguration.getConfig().getSocketServer { server ->
            server?.addConnectListener { client ->
                val sessionId = client.sessionId
                val user = User(session_id = sessionId.toString())
                listUserOnline.add(user)
                println("some connected -> ${client.sessionId}")
            }

            server?.addDisconnectListener { client ->
                listUserOnline.find { it.session_id == client.sessionId.toString() }?.apply {
                    println("$name disconnected")
                }
            }

            server?.addEventListener("user_connect", JSONObject::class.java) { client, data, ackSender ->
                println(data.toString())
                /*val user = data.toUser()
                listUserOnline.find { it.session_id == client.sessionId.toString() }.let {
                    it?.id = user.id
                    it?.name = user.name
                    it?.email = user.email
                    it?.phone = user.phone
                    it?.photo = user.photo
                    it?.driver_attribute = user.driver_attribute
                    it?.position = user.position

                    println("${it?.name} connected")
                }

                server.getClient(UUID.fromString(user.session_id)).sendEvent("socket_connection", true)

                println("session id from user -> ${UUID.fromString(user.session_id)} - origin -> ${client.sessionId}")*/
            }
        }
    }


    @PostMapping("/")
    fun attachSocket(): Responses {
        return Responses("ok", "try connecting")
    }
}