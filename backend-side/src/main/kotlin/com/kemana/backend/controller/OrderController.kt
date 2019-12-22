package com.kemana.backend.controller

import com.kemana.backend.model.OrderData
import com.kemana.backend.model.Responses
import com.kemana.backend.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/order")
class OrderController {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveOrder(@RequestBody orderData: OrderData): Responses {
        orderRepository.save(orderData)
        return Responses("ok", orderData)
    }

    @RequestMapping(value = ["/delete"], method = [RequestMethod.DELETE])
    fun deleteOrder(@RequestParam("id") id: String): Responses {
        val orderData = orderRepository.findOrderById(id)
        orderRepository.delete(orderData)
        return Responses("OK", "delete ok")
    }

}