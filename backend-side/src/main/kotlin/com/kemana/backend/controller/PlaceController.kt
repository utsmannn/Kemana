package com.kemana.backend.controller

import com.kemana.backend.model.OriginPlaces
import com.kemana.backend.model.Places
import com.kemana.backend.model.PlacesResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate


@RestController
@RequestMapping("/api/v1/place")
class PlaceController {

    private val restTemplate = RestTemplate()

    @RequestMapping(value = ["/search/{place_name}"], method = [RequestMethod.GET])
    fun getPlace(@PathVariable("place_name") placeName: String) : PlacesResponses? {
        val url = "https://api.mapbox.com/geocoding/v5/mapbox.places/$placeName.json?access_token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q"

        val originPlaceResponses = restTemplate.getForObject(url, OriginPlaces::class.java)
        val listFeature = originPlaceResponses?.features

        val listPlaces = listFeature?.map {
            return@map Places(it?.id, it?.placeName, listOf(it?.geometry?.coordinates?.get(0), it?.geometry?.coordinates?.get(1)))
        }

        return PlacesResponses(listPlaces?.size, listPlaces)
    }
}