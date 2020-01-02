package com.utsman.kemana.backend

import com.utsman.kemana.backend.controller.RabbitController
import com.utsman.kemana.backend.model.CheckPort
import com.utsman.kemana.backend.model.Responses
import com.utsman.kemana.backend.rabbit.Rabbit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.net.ServerSocket

@SpringBootApplication
@EnableEurekaClient
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}

@RestController
@RequestMapping("/api/v1")
class StartController {

	@Autowired
	lateinit var environment: Environment

	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	private val rabbitController = RabbitController()

	private var port = 0

	@RequestMapping(value = ["/check"], method = [RequestMethod.GET])
	fun check(): Responses {
		val name = environment.getProperty("spring.application.name")
		port = ServerSocket(0).use { it.localPort }
		println(port)
		val serverSocket = ServerSocket(port)
		rabbitController.startSocketServer(serverSocket)

		val RABBIT_URL = "amqp://user1:1234@192.168.43.193/%2F"
		//val RABBIT_URL = "amqp://user1:1234@192.168.1.13/%2F"
		Rabbit.setInstance("server-$name", RABBIT_URL) {
			rabbitController.startListen(mongoTemplate, environment)
		}

		return Responses("ok", CheckPort(port))
	}

	@RequestMapping(value = ["/request-order"], method = [RequestMethod.POST])
	fun requestOrder(): Responses {
		rabbitController.checkAvailableAndBid(mongoTemplate, environment, port)
		return Responses("ok", "waiting callback...")
	}
}