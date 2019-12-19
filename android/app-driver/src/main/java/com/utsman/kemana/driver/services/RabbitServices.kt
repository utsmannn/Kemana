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

import android.content.Intent
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.base.RxService
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.toast
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import org.json.JSONObject

class RabbitServices : RxService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Notify.listen(ConnectorRabbit::class.java, NotifyProvider(), Consumer {
            val passengerId = it.data.getString("passenger_id")
            val type = it.data.getInt("type")
            val data = it.data.getJSONObject("data")

            when (type) {
                Type.TRACKING -> {
                    val jsonObject = JSONObject()
                    jsonObject.apply {
                        put("type", Type.TRACKING)
                        put("data", data)
                    }

                    Rabbit.getInstance()?.publishTo(passengerId, jsonObject) {
                        toast("error, try again")
                    }
                }
            }
        })

        Rabbit.getInstance()?.listen { from, body ->
            logi("data is coming --> $body")

            val type = body.getInt("type")
            val data = body.getJSONObject("data")

            when (type) {
                Type.ORDER_REQUEST -> {
                    /*val objectSubs = ObjectOrderSubs(data)
                    Notify.send(objectSubs)
                    logi("from $from --> $body")*/

                    
                }
                Type.ORDER_CANCEL -> {
                    /*val orderCancel = OrderCancelSubs(true)
                    Notify.send(orderCancel)*/
                }
            }
        }

        return START_STICKY
    }
}