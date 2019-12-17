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

package com.utsman.kemana.driver.services

import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import org.json.JSONObject

data class ConnectorRabbit(val data: JSONObject)

fun Notify.sendToRabbit(passengerId: String, type: Int, data: JSONObject) {
    val body = JSONObject()
    body.apply {
        put("pass_id", passengerId)
        put("type", type)
        put("data", data)
    }

    send(ConnectorRabbit(body))
}

fun Notify.listenFromRabbit(type: Int, data: JSONObject) {
    listen(ConnectorRabbit::class.java, NotifyProvider(), Consumer {
        val typ = it.data.getInt("type")


    })
}