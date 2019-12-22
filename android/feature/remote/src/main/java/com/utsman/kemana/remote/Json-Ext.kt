/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.remote

import com.utsman.kemana.remote.driver.*
import com.utsman.kemana.remote.place.Places
import org.json.JSONObject

fun Places.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    val geoDrawUrl = geometryDrawUrl ?: "null"
    jsonObject.apply {
        put("id", id)
        put("placeName", placeName)
        put("addressName", addressName)
        put("lat", geometry?.get(0))
        put("lon", geometry?.get(1))
        put("geometryDrawUrl", geoDrawUrl)
    }

    return jsonObject
}

fun JSONObject.toPlace(): Places {
    val id = getString("id")
    val placeName = getString("placeName")
    val addressName = getString("addressName")
    val lat = getDouble("lat")
    val lon = getDouble("lon")
    val geometryDraw = getString("geometryDrawUrl")

    return Places(id, placeName, addressName, listOf(lat,lon), geometryDraw)
}

fun Passenger.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    val idNullsafe = id ?: "id"
    val positionNullsafe = position ?: Position(0.0, 0.0, 0.0)
    jsonObject.apply {
        put("id", idNullsafe)
        put("name", name)
        put("email", email)
        put("photoUrl", photoUrl)
        put("lat", positionNullsafe.lat)
        put("lon", positionNullsafe.lon)
    }

    return jsonObject
}

fun JSONObject.toPassenger(): Passenger {
    val id = getString("id")
    val name = getString("name")
    val email = getString("email")
    val photoUrl = getString("photoUrl")
    val lat = getDouble("lat")
    val lon = getDouble("lon")

    return Passenger(id, name, email, photoUrl, Position(lat, lon))
}


fun Driver.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    val idNullsafe = id ?: "id"
    val positionNullsafe = position ?: Position(0.0, 0.0, 0.0)
    jsonObject.apply {
        put("id", idNullsafe)
        put("name", name)
        put("email", email)
        put("photoUrl", photoUrl)
        put("lat", positionNullsafe.lat)
        put("lon", positionNullsafe.lon)
    }

    return jsonObject
}

fun JSONObject.toDriver(): Driver {
    val id = getString("id")
    val name = getString("name")
    val email = getString("email")
    val photoUrl = getString("photoUrl")
    val lat = getDouble("lat")
    val lon = getDouble("lon")

    return Driver(id, name, email, photoUrl, Position(lat, lon))
}

fun OrderData.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.apply {
        put("accepted", accepted)
        put("driver", attribute.driver?.toJSONObject())
        put("passenger", attribute.passenger?.toJSONObject())
        put("orderId", orderID)
        put("from", from?.toJSONObject())
        put("to", to?.toJSONObject())
    }

    return jsonObject
}

fun JSONObject.toOrderData(): OrderData {
    val accepted = getBoolean("accepted")
    val orderId = getString("orderId")
    val from = getJSONObject("from").toPlace()
    val to = getJSONObject("to").toPlace()
    val driver = getJSONObject("driver")
    val passenger = getJSONObject("passenger")
    val orderDataAttr = OrderDataAttr(driver.toDriver(), passenger.toPassenger())

    return OrderData(orderId, accepted, from, to, orderDataAttr)
}