package com.kemana.backend.controller

import com.kemana.backend.maputil.Bounding
import com.kemana.backend.model.DirectionOrigin
import com.kemana.backend.model.OriginPlaces
import com.kemana.backend.model.Places
import com.kemana.backend.model.PlacesResponses
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/v1/place")
class PlaceController {
    private val restTemplate = RestTemplate()

    @RequestMapping(value = ["/search"], method = [RequestMethod.GET])
    fun getPlaceBounding(
            @RequestParam("q") placeName: String,
            @RequestParam("from") coordinate: String,
            @RequestParam("radius") radius: Double): PlacesResponses? {

        val listCoordinate = coordinate.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()

        val bound = Bounding(radius)
        val bounding = bound.calculate(lat, lon)

        val minLon = bounding.getLonMin()
        val minLat = bounding.getLatMin()
        val maxLon = bounding.getLonMax()
        val maxLat = bounding.getLatMax()

        val boundingString = "$minLon,$minLat,$maxLon,$maxLat"

        val url = "https://api.mapbox.com/geocoding/v5/mapbox.places/$placeName.json?bbox=$boundingString&access_token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q"
        val originPlaceResponses = restTemplate.getForObject(url, OriginPlaces::class.java)
        val listFeature = originPlaceResponses?.features

        val listPlaces = listFeature?.map {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val toLat = it?.geometry?.coordinates?.get(1)
            val toLon = it?.geometry?.coordinates?.get(0)
            return@map Places(it?.id, it?.text, it?.placeName, listOf(toLat, toLon))
        }

        return PlacesResponses(listPlaces?.size, listPlaces)
    }

    @RequestMapping(value = ["/direction"], method = [RequestMethod.GET])
    fun getDirection(
            @RequestParam("from") from: String,
            @RequestParam("to") to: String
    ): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val fromReverse = from.reverseString()
        val toReverse = to.reverseString()

        val map = LinkedMultiValueMap<String, String>()
        map.add("coordinates", "$fromReverse;$toReverse")
        val url = "https://api.mapbox.com/directions/v5/mapbox/driving-traffic?access_token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q&exclude=motorway"

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
        println(request.body.toString())
        val responses = restTemplate.exchange(url, HttpMethod.POST, request, DirectionOrigin::class.java)
        return responses.body?.routes?.get(0)?.geometry
    }

    private fun String.reverseString(): String {
        val raw = split(",")
        return "${raw[1]},${raw[0]}"
    }
}