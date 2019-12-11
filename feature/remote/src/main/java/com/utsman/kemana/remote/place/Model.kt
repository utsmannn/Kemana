package com.utsman.kemana.remote.place

import com.google.gson.annotations.SerializedName

data class Places(
    val id: String?,
    val placeName: String?,
    val addressName: String?,
    val geometry: List<Double?>?,
    @SerializedName("geometry_draw_url")
    val geometryDrawUrl: String?
)

data class PlacesResponses(
    val size: Int?,
    val places: List<Places?>?
)