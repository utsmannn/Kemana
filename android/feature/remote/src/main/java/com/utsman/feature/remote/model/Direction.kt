package com.utsman.feature.remote.model

import com.google.gson.annotations.SerializedName

data class Direction(
    @SerializedName("from_coordinate")
    val fromCoordinate: List<Double>,
    @SerializedName("to_coordinate")
    val toCoordinate: List<Double>,
    val distance: Double,
    val geometry: String
)