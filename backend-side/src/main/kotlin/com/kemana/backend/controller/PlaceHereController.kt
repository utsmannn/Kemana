package com.kemana.backend.controller

import com.kemana.backend.maputil.Bounding
import com.kemana.backend.model.OriginPlaces
import com.kemana.backend.model.PlaceHere
import com.kemana.backend.model.Places
import com.kemana.backend.model.PlacesResponses
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate


@RestController
@RequestMapping("/api/v1/here")
class PlaceHereController {
    private val restTemplate = RestTemplate()

    @RequestMapping(value = ["/search"], method = [RequestMethod.GET])
    fun getPlaceBounding(
            @RequestParam("q") placeName: String,
            @RequestParam("from") coordinate: String,
            @RequestParam("radius") radius: Double): PlacesResponses? {

        val listCoordinate = coordinate.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()

        val url = "https://places.sit.ls.hereapi.com/places/v1/autosuggest?at=$lat,$lon&q=$placeName&apikey=EKZhNIBtjrjeYxqdyhCMQ1kxVc_O4QGfxEJLqWt0Hp0"

        val respon = restTemplate.getForObject(url, PlaceHere::class.java)
        val listRaw = respon?.results?.map {
            val toLat = it?.position?.get(0)
            val toLon = it?.position?.get(1)

            val geometryDrawUrl = "/api/v1/place/direction?from=$coordinate&to=$toLat,$toLon"
            return@map Places(it?.id, it?.title, it?.vicinity?.replace("<br/>", ", "), listOf(toLat, toLon), geometryDrawUrl)
        }

        val listResult = listRaw?.filter { it.id != null }

        return PlacesResponses(listResult?.size, listResult)
    }
}