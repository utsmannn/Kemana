package com.kemana.backend.controller

import com.kemana.backend.model.*
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
            @RequestParam("from") coordinate: String): PlacesResponses? {

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

    @RequestMapping(value = ["/direction"], method = [RequestMethod.POST])
    fun getDirection(
            @RequestParam("from") from: String,
            @RequestParam("to") to: String
    ): ResponsesDirection? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val fromReverse = from.reverseString()
        val toReverse = to.reverseString()

        val map = LinkedMultiValueMap<String, String>()
        map.add("coordinates", "$fromReverse;$toReverse")
        val url = "https://api.mapbox.com/directions/v5/mapbox/driving?access_token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q&exclude=toll"

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
        println(request.body.toString())
        val responses = restTemplate.exchange(url, HttpMethod.POST, request, DirectionOrigin::class.java)

        return ResponsesDirection(responses.body?.routes?.get(0)?.distance, responses.body?.routes?.get(0)?.geometry)
    }

    @RequestMapping(value = [""], method = [RequestMethod.GET])
    fun getPlace(@RequestParam("from") from: String): PlacesResponses? {
        val listCoordinate = from.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()
        val url = "https://reverse.geocoder.ls.hereapi.com/6.2/reversegeocode.json?prox=$lat,$lon&mode=retrieveAddresses&maxresults=3&apiKey=EKZhNIBtjrjeYxqdyhCMQ1kxVc_O4QGfxEJLqWt0Hp0"

        val originAddress = restTemplate.getForObject(url, AddressResponses::class.java)
        println(from)
        val listAddress = originAddress?.response?.view?.get(0)?.result?.map {
            it?.location?.address?.label
            
            return@map Places(
                    id = it?.location?.locationId,
                    placeName = it?.location?.address?.label,
                    addressName = it?.location?.address?.label,
                    geometry = listOf(lat, lon),
                    geometry_draw_url = null
            )
        }

        return PlacesResponses(listAddress?.size, listAddress)
    }

    private fun String.reverseString(): String {
        val raw = split(",")
        return "${raw[1]},${raw[0]}"
    }
}