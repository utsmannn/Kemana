package com.utsman.feature.remote.model

import com.google.gson.annotations.SerializedName

data class Place(
    var id: String? = null,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("address_name")
    val addressName: String,
    var geometry: List<Double>? = null
)

data class PlaceResponses(
    val size: Int,
    val places: List<Place>
)