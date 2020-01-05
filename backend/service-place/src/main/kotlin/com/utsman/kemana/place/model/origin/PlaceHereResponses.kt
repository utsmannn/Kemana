package com.utsman.kemana.place.model.origin

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceHereResponses(
        @JsonProperty("results")
        val results: List<Result?>?
)

data class Result(
        @JsonProperty("category")
        val category: String?,
        @JsonProperty("categoryTitle")
        val categoryTitle: String?,
        @JsonProperty("distance")
        val distance: Int?,
        @JsonProperty("highlightedTitle")
        val highlightedTitle: String?,
        @JsonProperty("highlightedVicinity")
        val highlightedVicinity: String?,
        @JsonProperty("href")
        val href: String?,
        @JsonProperty("id")
        val id: String?,
        @JsonProperty("position")
        val position: List<Double?>?,
        @JsonProperty("resultType")
        val resultType: String?,
        @JsonProperty("title")
        val title: String?,
        @JsonProperty("type")
        val type: String?,
        @JsonProperty("vicinity")
        val vicinity: String?
)