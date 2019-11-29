package com.kemana.backend.repository

import com.kemana.backend.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface DriverRepository : MongoRepository<User, String> {
    fun findDriverById(id: String) : User
    fun findDriverByEmail(email: String) : User
}