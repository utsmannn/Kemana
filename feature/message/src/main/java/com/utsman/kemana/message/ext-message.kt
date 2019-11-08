/*
 * Copyright 2019 Muhammad Utsman
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