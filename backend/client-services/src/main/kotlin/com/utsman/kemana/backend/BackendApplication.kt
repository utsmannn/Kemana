package com.utsman.kemana.backend

import com.utsman.kemana.backend.model.Responses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}

@RestController
@RequestMapping("/api/v1")
class CheckController {

	@Autowired
	lateinit var environment: Environment

	@RequestMapping(value = ["/check"], method = [RequestMethod.GET])
	fun check(): Responses {
		val name = environment.getProperty("spring.application.name")
		return Responses("ok", "check ok with name -> $name")
	}
}