package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.User
import com.utsman.kemana.backend.rabbit.Rabbit
import org.json.JSONObject
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class RabbitController {

    private var isOnline = false
    private var target = 0

    private var startTime = 0L

    private var socket: Socket? = null

    private var answered = false

    private lateinit var thread: Thread

    fun startListen(mongoTemplate: MongoTemplate, environment: Environment) {
        val clientId = environment.getProperty("spring.application.name")

        Rabbit.getInstance()?.listen { from, body ->
            println("data is ->> $from -> $body")
            val data = body.getJSONObject("body")
            when (data.getString("type")) {

                // NEED SOCKET FOR HEARTBEAT
                // wait from device
                /*"checking" -> {
                    if (from == clientId) {
                        val data = body.getJSONObject("data")
                        val online = data.getBoolean("online")
                        isOnline = online

                        if (online) {
                            sendingBid(mongoTemplate, target, clientId)
                        } *//*else {
                            target += 1
                            sendingBid(mongoTemplate, target, clientId)
                        }*//*
                    }
                }*/

                // from server client
                "bid" -> {
                    val bidOwner = data.getString("from")

                    // check available using socket

                    thread(name = "socket") {
                        println("waiting client answer")

                        val o = ObjectOutputStream(socket?.getOutputStream())
                        val i = ObjectInputStream(socket?.getInputStream())
                        val msg = i.readObject() as String
                        println("message from client -> $msg")
                        o.writeObject("bah ilah")

                        if (msg == "oke") {
                            //sendingBid(mongoTemplate, target, clientId!!)
                            bidToDevice(bidOwner, clientId)
                            answered = true
                            socket?.close()
                        } else {
                            println("offline")
                        }

                        Timer().schedule(10000) {
                            if (!answered) {
                                println("offline")
                                socket?.close()
                                target += 1
                                sendingBid(mongoTemplate, target, clientId!!)
                            }
                        }
                    }.run()

                    // send to device

                }

                // from device
                "order" -> {
                    /*val data = body.getBoolean("accepted")
                    if (data) {
                        // accepted
                        println("accepted")
                    } else {
                        target += 1
                        sendingBid(mongoTemplate, target, clientId!!)
                    }*/
                }
            }
        }
    }

    private fun bidToDevice(bidOwner: String?, clientId: String?) {
        val json = JSONObject()
        json.put("type", "order")
        json.put("body", bidOwner)

        Rabbit.getInstance()?.publishTo(clientId, json)
    }

    fun startSocketServer(server: ServerSocket) {

        thread(name = "socket") {
            this.socket = server.accept()
            println("socket stared")
        }.run()
    }

    // to device
    fun checkAvailableAndBid(mongoTemplate: MongoTemplate, environment: Environment, port: Int) {
        val clientId = environment.getProperty("spring.application.name")

        if (clientId != null) {
            sendingBid(mongoTemplate, target, clientId)
        }
    }

    // send bind
    private fun sendingBid(mongoTemplate: MongoTemplate, target: Int, clientId: String) {
        val driverActive = mongoTemplate.findAll<User>("driver_active")
        val emails = driverActive.map { it.email }
        val size = emails.size - 1

        // to server client target
        if (target <= size) {
            val email = emails[target]

            val jsonData = JSONObject()
            jsonData.put("from", clientId)
            jsonData.put("type", "bid")

            println("try sending to target -> $jsonData")
            //Rabbit.getInstance()?.publishTo(email, json)
            Rabbit.getInstance()?.publishTo("server-kucingapes.uts@gmail.com", jsonData)


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