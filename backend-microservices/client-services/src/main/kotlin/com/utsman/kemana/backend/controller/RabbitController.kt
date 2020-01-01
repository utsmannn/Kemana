package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.User
import com.utsman.kemana.backend.rabbit.Rabbit
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll

class RabbitController {

    @Autowired
    lateinit var environment: Environment

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    private var isOnline = false
    private var target = 0

    fun startListen() {
        val clientId = environment.getProperty("spring.application.name")

        Rabbit.getInstance()?.listen { from, body ->
            when (body.getString("type")) {
                // from device
                "checking" -> {
                    if (from == clientId) {
                        val data = body.getBoolean("online")
                        isOnline = data

                        if (data) {
                            sendingBid(target, clientId)
                        }
                    }
                }

                // from server client
                "bid" -> {
                    val data = body.getJSONObject("body")
                    val bidOwner = data.getString("from")

                    // send to device
                    val json = JSONObject()
                    json.put("type", "order")
                    json.put("body", bidOwner)

                    Rabbit.getInstance()?.publishTo(clientId, json)
                }

                // from device
                "order" -> {
                    val data = body.getBoolean("accepted")
                    if (data) {
                        // accepted
                        println("accepted")
                    } else {
                        target += 1
                        sendingBid(target, clientId!!)
                    }
                }
            }
        }
    }

    // to device
    fun checkAvailableAndBid() {
        val clientId = environment.getProperty("spring.application.name")
        val json = JSONObject()
        val jsonBody = JSONObject()

        jsonBody.put("message", "isAlive")
        json.put("type", "checking")
        json.put("body", jsonBody)

        Rabbit.getInstance()?.publishTo(clientId, json)
    }

    // to server client
    private fun sendingBid(target: Int, clientId: String) {
        val driverActive = mongoTemplate.findAll<User>("driver_active")
        val email = driverActive.map { it.email }[target]

        val json = JSONObject()
        val jsonBody = JSONObject()
        jsonBody.put("from", clientId)

        json.put("type", "bid")
        json.put("body", jsonBody)

        Rabbit.getInstance()?.publishTo(email, json)
    }

}