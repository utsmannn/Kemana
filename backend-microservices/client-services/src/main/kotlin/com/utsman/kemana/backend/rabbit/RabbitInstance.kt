package com.utsman.kemana.backend.rabbit

import org.json.JSONObject
import java.lang.Exception

interface RabbitInstance {
    fun listen(msg: (from: String, body: JSONObject) -> Unit)
    fun publishTo(id: String?, msg: JSONObject, error: ((Exception) -> Unit)? = null)
}