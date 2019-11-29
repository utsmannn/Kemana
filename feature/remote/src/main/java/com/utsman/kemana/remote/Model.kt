package com.utsman.kemana.remote

open class User(var id: String? = null,
                val name: String,
                val email: String,
                val photoUrl: String,
                var lat: Double? = 0.0,
                var lon: Double? = 0.0,
                var attribute: Attribute? = null)

data class Attribute(var vehiclesType: String? = "passenger",
                     var vehiclesPlat: String? = "passenger",
                     var onOrder: Boolean = false,
                     var angle: Double? = 0.0)

data class Responses(val message: String,
                     val data: Any)