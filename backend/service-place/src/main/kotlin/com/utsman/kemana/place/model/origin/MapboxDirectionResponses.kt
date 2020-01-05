package com.utsman.kemana.place.model.origin

import com.fasterxml.jackson.annotation.JsonProperty

data class MapboxDirectionResponses(
        @JsonProperty("code")
        val code: String?,
        @JsonProperty("routes")
        val routes: List<Route?>?,
        @JsonProperty("uuid")
        val uuid: String?,
        @JsonProperty("waypoints")
        val waypoints: List<Waypoint?>?
)

data class Route(
        @JsonProperty("distance")
        val distance: Double?,
        @JsonProperty("duration")
        val duration: Double?,
        @JsonProperty("geometry")
        val geometry: String?,
        @JsonProperty("legs")
        val legs: List<Leg?>?,
        @JsonProperty("weight")
        val weight: Double?,
        @JsonProperty("weight_name")
        val weightName: String?
)

data class Leg(
        @JsonProperty("distance")
        val distance: Double?,
        @JsonProperty("duration")
        val duration: Double?,
        @JsonProperty("steps")
        val steps: List<Any?>?,
        @JsonProperty("summary")
        val summary: String?,
        @JsonProperty("weight")
        val weight: Double?
)

data class Waypoint(
        @JsonProperty("distance")
        val distance: Double?,
        @JsonProperty("location")
        val location: List<Double?>?,
        @JsonProperty("name")
        val name: String?
)