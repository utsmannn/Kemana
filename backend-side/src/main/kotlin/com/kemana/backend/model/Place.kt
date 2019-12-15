package com.kemana.backend.model


import com.fasterxml.jackson.annotation.JsonProperty

data class OriginPlaces(
        @JsonProperty("attribution")
        val attribution: String?,
        @JsonProperty("features")
        val features: List<Feature?>?,
        @JsonProperty("query")
        val query: List<String?>?,
        @JsonProperty("type")
        val type: String?
)

data class Feature(
        @JsonProperty("center")
        val center: List<Double?>?,
        @JsonProperty("context")
        val context: List<Context?>?,
        @JsonProperty("geometry")
        val geometry: Geometry?,
        @JsonProperty("id")
        val id: String?,
        @JsonProperty("place_name")
        val placeName: String?,
        @JsonProperty("place_type")
        val placeType: List<String?>?,
        @JsonProperty("properties")
        val properties: Properties?,
        @JsonProperty("relevance")
        val relevance: Int?,
        @JsonProperty("text")
        val text: String?,
        @JsonProperty("type")
        val type: String?
)

data class Context(
        @JsonProperty("id")
        val id: String?,
        @JsonProperty("short_code")
        val shortCode: String?,
        @JsonProperty("text")
        val text: String?,
        @JsonProperty("wikidata")
        val wikidata: String?
)

data class Geometry(
        @JsonProperty("coordinates")
        val coordinates: List<Double?>?,
        @JsonProperty("type")
        val type: String?
)

data class Properties(
        @JsonProperty("address")
        val address: String?,
        @JsonProperty("category")
        val category: String?,
        @JsonProperty("landmark")
        val landmark: Boolean?
)

data class Places(
        val id: String?,
        val placeName: String?,
        val addressName: String?,
        val geometry: List<Double?>?,
        val geometry_draw_url: String?
)

data class PlacesResponses(
        val size: Int?,
        val places: List<Places?>?
)