package com.utsman.kemana.backend.model.place

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