package com.kemana.backend.repository

import com.kemana.backend.model.DriverEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface DriverDbRepository : MongoRepository<DriverEntity, String> {
    fun findDriverById(id: String) : DriverEntity?
    fun findDriverByEmail(email: String) : DriverEntity?
}