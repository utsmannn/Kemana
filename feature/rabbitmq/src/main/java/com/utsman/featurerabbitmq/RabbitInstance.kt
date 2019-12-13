package com.utsman.featurerabbitmq

import io.reactivex.disposables.Disposable
import org.json.JSONObject

interface RabbitInstance {
    fun listen(msg: (from: String, body: JSONObject) -> Unit) : Disposable
    fun publishTo(id: String, autoClear: Boolean, msg: JSONObject): Disposable
}