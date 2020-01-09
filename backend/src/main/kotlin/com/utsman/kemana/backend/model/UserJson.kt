package com.utsman.kemana.backend.model

import org.json.simple.JSONObject

fun User.toJSONObject(): JSONObject {
    return JSONObject().apply {
        put("id", id)
        put("session_id", session_id.toString())
        put("name", name)
        put("email", email)
        put("phone", phone)
        put("photo", photo)
        put("position", position?.toJSONObject())
        put("driver_attribute", driver_attribute?.toJSONObject())
    }
}

fun JSONObject.toUser(): User {
    return User(
            id = get("id") as String?,
            session_id = get("session_id") as String?,
            name = get("name") as String?,
            email = get("email") as String?,
            phone = get("phone") as String?,
            photo = get("photo") as String?,
            position = (get("position") as JSONObject?)?.toPosition(),
            driver_attribute = (get("driver_attribute") as JSONObject?)?.toDriverAttribute()
    )
}

fun DriverAttribute.toJSONObject(): JSONObject {
    return JSONObject().apply {
        put("num", num)
        put("type", type)
        put("color", color)
    }
}

fun JSONObject.toDriverAttribute(): DriverAttribute {
    return DriverAttribute(
            num = get("num") as String?,
            type = get("type") as String?,
            color = get("color") as String?
    )
}

fun Position.toJSONObject(): JSONObject {
    return JSONObject().apply {
        put("lat", lat)
        put("lon", lon)
    }
}

fun JSONObject.toPosition(): Position {
    return Position(
            lat = get("lat") as Double?,
            lon = get("lon") as Double?
    )
}