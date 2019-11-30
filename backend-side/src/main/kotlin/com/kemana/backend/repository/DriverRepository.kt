package com.kemana.backend.repository

import com.kemana.backend.model.Driver
import org.springframework.data.mongodb.repository.MongoRepository

interface DriverRepository : MongoRepository<Driver, String> {
    fun findDriverById(id: String) : Driver?
    fun findDriverByEmail(email: String) : Driver
}