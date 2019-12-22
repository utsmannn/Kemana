package com.kemana.backend.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "driver_active")
open class Driver(var id: String? = null,
                  val name: String,
                  val email: String,
                  val photoUrl: String,
                  var position: Position? = null,
                  var attribute: Attribute? = null)

@Document(collection = "driver_db")
open class DriverEntity(
        var id: String? = null,
        val name: String,
        val email: String,
        val photoUrl: String,
        var position: Position? = null,
        var attribute: Attribute? = null
)

data class Position(var lat: Double? = 0.0,
                    var lon: Double? = 0.0,
                    var angle: Double? = 0.0)

data class Attribute(var vehiclesType: String? = "",
                     var vehiclesPlat: String? = "")
