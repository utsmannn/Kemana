package com.kemana.eureka

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/service")
class ClientController {

    @RequestMapping("/start/{email}", method = [RequestMethod.POST])
    fun startService(@PathVariable("email") email: String): Responses {
        val processBuilder = ProcessBuilder()
        println("try start service for $email")
        processBuilder.command("java", "-jar", "backend-0.0.1.jar", "--spring.application.name=$email")
        processBuilder.start()

        return Responses("OK", "Please wait your service")
    }
}

data class Responses(val message: String, val status: String)