package com.utsman.kemana.backend.model.place.origin


import com.fasterxml.jackson.annotation.JsonProperty

data class AddressResponses(
    @JsonProperty("Response")
    val response: Response?
)

data class Response(
        @JsonProperty("MetaInfo")
        val metaInfo: MetaInfo?,
        @JsonProperty("View")
        val view: List<View?>?
)

data class MetaInfo(
        @JsonProperty("NextPageInformation")
        val nextPageInformation: String?,
        @JsonProperty("Timestamp")
        val timestamp: String?
)

data class View(
        @JsonProperty("Result")
        val result: List<ResultAddress?>?,
        @JsonProperty("_type")
        val type: String?,
        @JsonProperty("ViewId")
        val viewId: Int?
)

data class ResultAddress(
        @JsonProperty("Distance")
        val distance: Double?,
        @JsonProperty("Location")
        val location: Location?,
        @JsonProperty("MatchLevel")
        val matchLevel: String?,
        @JsonProperty("MatchQuality")
        val matchQuality: MatchQuality?,
        @JsonProperty("Relevance")
        val relevance: Double?
)

data class Location(
        @JsonProperty("Address")
        val address: Address?,
        @JsonProperty("DisplayPosition")
        val displayPosition: DisplayPosition?,
        @JsonProperty("LocationId")
        val locationId: String?,
        @JsonProperty("LocationType")
        val locationType: String?,
        @JsonProperty("MapReference")
        val mapReference: MapReference?,
        @JsonProperty("MapView")
        val mapView: MapView?
)

data class Address(
        @JsonProperty("AdditionalData")
        val additionalData: List<AdditionalData?>?,
        @JsonProperty("City")
        val city: String?,
        @JsonProperty("Country")
        val country: String?,
        @JsonProperty("County")
        val county: String?,
        @JsonProperty("District")
        val district: String?,
        @JsonProperty("Label")
        val label: String?,
        @JsonProperty("PostalCode")
        val postalCode: String?,
        @JsonProperty("Street")
        val street: String?,
        @JsonProperty("Subdistrict")
        val subdistrict: String?
) {
    data class AdditionalData(
            @JsonProperty("key")
            val key: String?,
            @JsonProperty("value")
            val value: String?
    )
}

data class DisplayPosition(
        @JsonProperty("Latitude")
        val latitude: Double?,
        @JsonProperty("Longitude")
        val longitude: Double?
)

data class MapReference(
        @JsonProperty("CountryId")
        val countryId: String?,
        @JsonProperty("CountyId")
        val countyId: String?,
        @JsonProperty("DistrictId")
        val districtId: String?,
        @JsonProperty("ReferenceId")
        val referenceId: String?,
        @JsonProperty("SideOfStreet")
        val sideOfStreet: String?,
        @JsonProperty("Spot")
        val spot: Double?
)

data class MapView(
        @JsonProperty("BottomRight")
        val bottomRight: BottomRight?,
        @JsonProperty("TopLeft")
        val topLeft: TopLeft?
)

data class BottomRight(
        @JsonProperty("Latitude")
        val latitude: Double?,
        @JsonProperty("Longitude")
        val longitude: Double?
)

data class TopLeft(
        @JsonProperty("Latitude")
        val latitude: Double?,
        @JsonProperty("Longitude")
        val longitude: Double?
)

data class MatchQuality(
        @JsonProperty("City")
        val city: Double?,
        @JsonProperty("Country")
        val country: Double?,
        @JsonProperty("County")
        val county: Double?,
        @JsonProperty("District")
        val district: Double?,
        @JsonProperty("PostalCode")
        val postalCode: Double?,
        @JsonProperty("Street")
        val street: List<Double?>?,
        @JsonProperty("Subdistrict")
        val subdistrict: Double?
)