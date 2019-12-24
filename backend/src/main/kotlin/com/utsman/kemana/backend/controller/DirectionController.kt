package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.direction.origin.DirectionOrigin
import com.utsman.kemana.backend.model.direction.DirectionResponses
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/v1/direction")
class DirectionController {
    private val restTemplate = RestTemplate()

    @RequestMapping(method = [RequestMethod.GET])
    fun getDirection(
            @RequestParam("from") from: String,
            @RequestParam("to") to: String,
            @RequestParam("token") token: String
    ): DirectionResponses? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val fromReverse = from.reverseString()
        val toReverse = to.reverseString()

        val map = LinkedMultiValueMap<String, String>()
        map.add("coordinates", "$fromReverse;$toReverse")

        //val url = "https://api.mapbox.com/directions/v5/mapbox/driving?access_token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q&exclude=toll"
        val url = "https://api.mapbox.com/directions/v5/mapbox/driving?access_token=$token&exclude=toll"

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
        println(request.body.toString())
        val responses = restTemplate.exchange(url, HttpMethod.POST, request, DirectionOrigin::class.java)

        return DirectionResponses(responses.body?.routes?.get(0)?.distance, responses.body?.routes?.get(0)?.geometry)
    }

    private fun String.reverseString(): String {
        val raw = split(",")
        return "${raw[1]},${raw[0]}"
    }
}