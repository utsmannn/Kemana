package com.utsman.kemana.backend.model.place

data class Places(
        val id: String?,
        val placeName: String?,
        val addressName: String?,
        val geometry: List<Double?>?
)

data class PlacesResponses(
        val size: Int?,
        val places: List<Places?>?
)