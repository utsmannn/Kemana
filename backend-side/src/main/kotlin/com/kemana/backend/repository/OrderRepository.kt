package com.kemana.backend.repository

import com.kemana.backend.model.OrderData
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<OrderData, String> {
    fun findOrderById(id: String) : OrderData
}
