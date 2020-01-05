package com.utsman.kemana.socketserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SocketServerApplication

fun main(args: Array<String>) {
	runApplication<SocketServerApplication>(*args)
}

@RestController
class Controller {

	@RequestMapping("/start", method = [RequestMethod.GET])
	fun start(): Responses {
		val server = SocketServer()
		server.start()
		return Responses("ok", "socket ready")
	}
}

data class Responses(val message: String, val data: Any)