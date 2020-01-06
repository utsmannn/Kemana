package com.utsman.kemana.backend.model

import com.mongodb.lang.Nullable
import org.springframework.data.annotation.Id


data class UserTest(
        @Id
        var id: String? = null,
        val name: String? = null
)

data class User(
        @Id
        var id: String? = null,
        var name: String? = null,
        var email: String? = null,
        var phone: String? = null,
        val photo: String? = null,
        @Nullable
        var position: Position? = null,
        @Nullable
        var driver_attribute: DriverAttribute? = null
)

data class DriverAttribute(
        val num: String? = null,
        val type: String? = null,
        val color: String? = null
)

data class Position(
        val lat: Double? = null,
        val lon: Double? = null
)