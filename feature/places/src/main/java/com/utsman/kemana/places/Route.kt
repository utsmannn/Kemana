package com.utsman.kemana.places

data class Route(val routes: List<Routes>,
                 val uuid: String,
                 val code: String,
                 val waypoints: List<WayPoints>)

data class Routes(val geometry: String,
                  val distance: Double,
                  val duration: Double)

data class WayPoints(val location: List<Double>)