package com.kemana.backend.model


import com.fasterxml.jackson.annotation.JsonProperty

data class AddressHere(
        @JsonProperty("Response")
        val response: Response?
)

data class Response(
        @JsonProperty("View")
        val view: List<View?>?
)

data class Address(
        val label: String?
)

data class Location(
        @JsonProperty("Address")
        val address: Address?
)

data class ResultAddress(
        @JsonProperty("Location")
        val location: Location?
)

data class MetaInfo(
        @JsonProperty("NextPageInformation")
        val nextPageInformation: String?,
        @JsonProperty("Timestamp")
        val timestamp: String?
)

data class View(
        @JsonProperty("Result")
        val result: List<ResultAddress?>?
)