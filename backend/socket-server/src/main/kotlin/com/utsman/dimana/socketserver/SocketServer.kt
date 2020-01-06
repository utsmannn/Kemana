package com.utsman.dimana.socketserver

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.json.simple.JSONObject

class SocketServer  {

    fun startSocket() {
        val config = Configuration()
        config.hostname = "localhost"
        config.port = 8080

        val server = SocketIOServer(config)
        val listClient: MutableList<SocketObject> = mutableListOf()

        server.addEventListener("name", String::class.java) { client, data, ackSender ->
            listClient.find { it.clientId == client?.sessionId.toString() }?.apply {
                name = data
                println("$name is connected")
            }
            server.broadcastOperations.sendEvent("test", data)
        }

        /*server.addEventListener("add_person", Person::class.java) { client, data, ackSender ->
            println("$data")
            listPerson.find { it.id == client?.sessionId.toString() }?.apply {
                println("${data.name} is connected")

                name = data.name
                imageUrl = data.imageUrl
                location = data.location
            }
            //server.broadcastOperations.sendEvent("add_person", listPerson)
        }*/

       /* server.addEventListener("person", JSONObject::class.java) { client, data, ackSender ->
            val person = data.toPerson()
            listPerson.find { it.id == client?.sessionId.toString() }?.apply {
                println("${person.name} is connected")

                name = person.name
                imageUrl = person.imageUrl
                lat = person.lat
                lon = person.lon
            }

            //server.broadcastOperations.sendEvent("add_person", listPerson.find { it.id == client.sessionId.toString()})
            server.broadcastOperations.sendEvent("all_person", listPerson)
        }*/

        server.addDisconnectListener { client ->
            /*listPerson.find { it.id == client.sessionId.toString() }?.apply {
                println("$name id disconnected")
            }*/

            server.broadcastOperations.sendEvent("remove_person", client.sessionId.toString())
        }

        server.addConnectListener {
            val clientId = it.sessionId
            val socketObject = SocketObject(clientId = clientId.toString())
            listClient.add(socketObject)

            /*val person = Person(id = it.sessionId.toString())
            listPerson.add(person)*/
        }

        println("running")
        server.start()
    }
}

data class SocketObject(var clientId: String? = null, var name: String? = null)