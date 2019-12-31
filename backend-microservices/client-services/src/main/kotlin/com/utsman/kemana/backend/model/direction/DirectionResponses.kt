package com.utsman.kemana.backend.model.direction

data class DirectionResponses(
        val from_coordinate: List<Double>?,
        val to_coordinate: List<Double>?,
        val distance: Double?,
        val geometry: String?
)