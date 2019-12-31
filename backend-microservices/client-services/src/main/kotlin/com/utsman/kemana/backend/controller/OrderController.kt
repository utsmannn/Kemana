package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.Responses
import com.utsman.kemana.backend.model.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/order")
class OrderController {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @RequestMapping("/save", method = [RequestMethod.POST])
    fun saveOrder(@RequestBody order: Order): Responses {
        order.id = UUID.nameUUIDFromBytes(System.currentTimeMillis().toString().toByteArray()).toString()
        order.time = System.currentTimeMillis()
        mongoTemplate.save(order, "order_data")
        return Responses("ok", order)
    }

    @RequestMapping(method = [RequestMethod.GET])
    fun getOrder(@RequestParam("id") id: String): Responses {
        val order = mongoTemplate.findById<Order>(id, "order_data")
        return if (order != null) {
            Responses("ok", order)
        } else {
            Responses("fail", "order not found")
        }
    }

    @RequestMapping("/delete", method = [RequestMethod.DELETE])
    fun deleteOrder(@RequestParam("id") id: String): Responses {
        val order = mongoTemplate.findById<Order>(id, "order_data")
        return if (order != null) {
            mongoTemplate.remove(order, "order_data")
            Responses("ok", "deleted")
        } else {
            Responses("order not found")
        }
    }
}