package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.place.*
import com.utsman.kemana.backend.model.place.origin.AddressResponses
import com.utsman.kemana.backend.model.place.origin.PlaceHere
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/v1/place")
class PlaceController {

    private val restTemplate = RestTemplate()

    @RequestMapping(value = ["/search"], method = [RequestMethod.GET])
    fun searchPlace(
            @RequestParam("q") placeName: String,
            @RequestParam("from") coordinate: String,
            @RequestParam("apikey") apikey: String): PlacesResponses? {

        val listCoordinate = coordinate.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()

        val url = "https://places.sit.ls.hereapi.com/places/v1/autosuggest?at=$lat,$lon&q=$placeName&apikey=$apikey"

        val respon = restTemplate.getForObject(url, PlaceHere::class.java)
        val listRaw = respon?.results?.map {
            val toLat = it?.position?.get(0)
            val toLon = it?.position?.get(1)

            return@map Places(it?.id, it?.title, it?.vicinity?.replace("<br/>", ", "), listOf(toLat, toLon))
        }

        val listResult = listRaw?.filter { it.id != null }

        return PlacesResponses(listResult?.size, listResult)
    }

    @RequestMapping(method = [RequestMethod.GET])
    fun getPlace(
            @RequestParam("from") from: String,
            @RequestParam("apikey") key: String
    ): PlacesResponses? {
        val listCoordinate = from.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()

        val url = "https://reverse.geocoder.ls.hereapi.com/6.2/reversegeocode.json?prox=$lat,$lon&mode=retrieveAddresses&maxresults=3&apiKey=$key"

        val originAddress = restTemplate.getForObject(url, AddressResponses::class.java)
        println(from)

        val listAddress = originAddress?.response?.view?.get(0)?.result?.map {
            val street = it?.location?.address?.street
            val district = it?.location?.address?.district
            val city = it?.location?.address?.city

            val address = "$street, $district, $city".replace("null, ", "")
            
            return@map Places(
                    id = it?.location?.locationId,
                    place_name = address,
                    address_name = address,
                    geometry = listOf(lat, lon)
            )
        }

        return PlacesResponses(listAddress?.size, listAddress)
    }
}