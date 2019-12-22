//package com.kemana.backend
//
//import com.kemana.backend.model.Places
//import com.kemana.backend.model.Position
//import com.kemana.backend.model.RequestOrder
//import com.kemana.backend.model.User
//import org.json.JSONObject
//
//fun Places.toJSONObject(): JSONObject {
//    val jsonObject = org.json.JSONObject()
//    val geoDrawUrl = geometryDrawUrl ?: ""
//    jsonObject.apply {
//        put("id", id)
//        put("placeName", placeName)
//        put("addressName", addressName)
//        put("lat", geometry?.get(0))
//        put("lon", geometry?.get(1))
//        put("geometryDrawUrl", geoDrawUrl)
//    }
//
//    return jsonObject
//}
//
//fun JSONObject.toPlace(): Places {
//    val id = getString("id")
//    val placeName = getString("placeName")
//    val addressName = getString("addressName")
//    val lat = getDouble("lat")
//    val lon = getDouble("lon")
//    val geometryDraw = getString("geometry_draw_url")
//
//    return Places(id, placeName, addressName, listOf(lat,lon), geometryDraw)
//}
//
//fun User.toJSONObject(): JSONObject {
//    val jsonObject = JSONObject()
//    val idNullsafe = id ?: "id"
//    val positionNullsafe = position ?: Position(0.0, 0.0, 0.0)
//    jsonObject.apply {
//        put("id", idNullsafe)
//        put("name", name)
//        put("email", email)
//        put("photoUrl", photoUrl)
//        put("lat", positionNullsafe.lat)
//        put("lon", positionNullsafe.lon)
//    }
//
//    return jsonObject
//}
//
//fun RequestOrder.toJSONObject(): JSONObject {
//    val jsonObject = JSONObject()
//    jsonObject.apply {
//        put("id", id)
//        put("from", from?.toJSONObject())
//        put("to", to?.toJSONObject())
//        put("passenger", passenger?.toJSONObject())
//        put("distance", distance)
//    }
//
//    return jsonObject
//}
//
//fun JSONObject.toUser(): User {
//    val id = getString("id")
//    val name = getString("name")
//    val email = getString("email")
//    val photoUrl = getString("photoUrl")
//    val lat = getDouble("lat")
//    val lon = getDouble("lon")
//
//    return User(id, name, email, photoUrl, Position(lat, lon))
//}
//
//fun JSONObject.toRequestOrder(): RequestOrder {
//    val id = getString("id")
//    val from = getJSONObject("from")
//    val to = getJSONObject("to")
//    val passenger = getJSONObject("passenger")
//    val distance = getDouble("distance")
//
//    return RequestOrder(id, from.toPlace(), to.toPlace(), passenger.toUser(), distance)
//}