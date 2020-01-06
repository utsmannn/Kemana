package com.utsman.dimana.socketserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SocketServerApplication

fun main(args: Array<String>) {
	runApplication<SocketServerApplication>(*args)
}
