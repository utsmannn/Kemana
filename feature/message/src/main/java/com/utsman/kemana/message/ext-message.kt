package com.utsman.kemana.message

import org.json.JSONObject

fun JSONObject.toOrderData(): OrderData {
    val userId = get("user_id") as String
    val username = get("user_name") as String
    val userImg = get("user_img_url") as String
    val fromLat = get("from_lat") as Double
    val fromLng = get("from_lng") as Double
    val toLat = get("to_lat") as Double
    val toLng = get("to_lng") as Double
    val distance = get("distance") as String

    return OrderData(userId, username, userImg, fromLat, fromLng, toLat, toLng, distance.toDouble())
}

fun OrderData.toJSONObject(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.apply {
        put("user_id", userId)
        put("user_name", username)
        put("user_img_url", userImg)
        put("from_lat", fromLat)
        put("from_lng", fromLng)
        put("to_lat", toLat)
        put("to_lng", toLng)
        put("distance", distance.toString())
    }

    return jsonObject
}