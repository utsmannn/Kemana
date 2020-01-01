package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.User
import com.utsman.kemana.backend.rabbit.Rabbit
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.concurrent.schedule

class RabbitController {

    private var isOnline = false
    private var target = 0

    private var startTime = 0L

    fun startListen(mongoTemplate: MongoTemplate, environment: Environment) {
        val clientId = environment.getProperty("spring.application.name")

        Rabbit.getInstance()?.listen { from, body ->
            when (body.getString("type")) {

                // NEED SOCKET FOR HEARTBEAT
                // wait from device
                "checking" -> {
                    if (from == clientId) {
                        val data = body.getJSONObject("data")
                        val online = data.getBoolean("online")
                        isOnline = online

                        if (online) {
                            sendingBid(mongoTemplate, target, clientId)
                        } /*else {
                            target += 1
                            sendingBid(mongoTemplate, target, clientId)
                        }*/
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
                        sendingBid(mongoTemplate, target, clientId!!)
                    }
                }
            }
        }
    }

    // to device
    fun checkAvailableAndBid(environment: Environment) {
        val clientId = environment.getProperty("spring.application.name")
        val json = JSONObject()
        val jsonData = JSONObject()

        startTime = System.currentTimeMillis()
        jsonData.put("message", "isAlive")
        jsonData.put("time", System.currentTimeMillis())

        json.put("type", "checking")
        json.put("data", jsonData)

        Rabbit.getInstance()?.publishTo(clientId, json)
    }

    // send bind
    private fun sendingBid(mongoTemplate: MongoTemplate, target: Int, clientId: String) {
        val driverActive = mongoTemplate.findAll<User>("driver_active")
        val emails = driverActive.map { it.email }
        val size = emails.size - 1

        // to server client target
        if (target <= size) {
            val email = emails[target]

            val json = JSONObject()
            val jsonData = JSONObject()
            jsonData.put("from", clientId)

            json.put("type", "bid")
            json.put("data", jsonData)

            Rabbit.getInstance()?.publishTo(email, json)

            // to server client owner
        } else {
            val json = JSONObject()
            val jsonData = JSONObject()
            jsonData.put("finding", "failed")
            json.put("type", "order")
            json.put("data", jsonData)

            //Rabbit.getInstance()?.publishTo(clientId, jsonData)
            Rabbit.getInstance()?.publishTo("kucingapes.uts@gmail.com", jsonData)
        }
    }

}