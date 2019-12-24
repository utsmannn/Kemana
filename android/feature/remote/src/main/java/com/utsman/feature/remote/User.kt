package com.utsman.feature.remote

import com.google.gson.annotations.SerializedName

data class User(
    var id: String? = null,
    val name: String,
    val email: String,
    var phone: String? = null,
    var photo: String? = null,
    var position: Position? = null,
    @SerializedName("driver_attribute")
    var driverAttribute: DriverAttribute? = null
)

data class Position(
    val lat: Double,
    val lon: Double
)

data class DriverAttribute(
    val num: String,
    val type: String,
    val color: String
)

data class UserResponses(
    val message: String,
    val data: User
)

data class UserDeletedResponses(
    val message: String,
    val data: String?
)