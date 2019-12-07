package com.kemana.backend.controller

import com.kemana.backend.maputil.Bounding
import com.kemana.backend.model.OriginPlaces
import com.kemana.backend.model.Places
import com.kemana.backend.model.PlacesResponses
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
            return@map Places(it?.id, it?.text, it?.placeName, listOf(it?.geometry?.coordinates?.get(1), it?.geometry?.coordinates?.get(0)))
        }

        return PlacesResponses(listPlaces?.size, listPlaces)
    }
}