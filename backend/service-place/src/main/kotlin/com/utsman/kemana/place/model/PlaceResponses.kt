package com.utsman.kemana.place.model

data class Places(
        val id: String?,
        val place_name: String?,
        val address_name: String?,
        val geometry: List<Double?>?
)

data class PlacesResponses(
        val size: Int?,
        val places: List<Places?>?
)