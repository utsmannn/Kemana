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

package com.utsman.featurerabbitmq

import androidx.lifecycle.MutableLiveData
import com.rabbitmq.client.*
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException
import kotlin.Exception

@Suppress("UNCHECKED_CAST")
class Rabbit private constructor(private var connection: Connection?) {

    private val liveMsg = MutableLiveData<JSONObject>()
    private val liveError = MutableLiveData<Exception>()

    private fun setupInstance() : RabbitInstance {
        return object : RabbitInstance {
            override fun listen(msg: (from: String, body: JSONObject) -> Unit): Disposable {
                logi("rabbit try create new channel")
                return Observable.just(connection)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        val channel = it?.createChannel()
                        channel?.queueDeclare(id, false, false, true, null)

                        logi("rabbit channel created -> ${channel?.channelNumber}")
                        channel?.exchangeDeclare("kemana-2", "fanout")
                        channel?.queueBind(id, "kemana-2", id)

                        channel?.basicConsume(id, true, object : DefaultConsumer(channel) {
                            override fun handleDelivery(
                                consumerTag: String?,
                                envelope: Envelope?,
                                properties: AMQP.BasicProperties?,
                                body: ByteArray
                            ) {
                                super.handleDelivery(consumerTag, envelope, properties, body)
                                val delivery = JSONObject(String(body, Charset.forName("utf-8")))

                                liveMsg.postValue(delivery)
                            }
                        })
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        it.printStackTrace()
                    }
                    .doOnNext {
                        liveMsg.observeForever {
                            val id = it.getString("id")
                            val msgBody = it.getJSONObject("body")
                            logi("data string is -> $msgBody")
                            msg.invoke(id, msgBody)
                        }
                    }
                    .subscribe()
            }

            override fun publishTo(id: String, msg: JSONObject, error: (Exception) -> Unit): Disposable {
                return Observable.just(connection)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        try {
                            val channel = it?.createChannel()
                            channel?.queueDeclare(id, false, false, true, null)

                            channel?.exchangeDeclare("kemana-2", "fanout")
                            channel?.queueBind(id, "kemana-2", id)

                            val jsonBody = JSONObject()
                            jsonBody.put("id", Rabbit.id)
                            jsonBody.put("body", msg)

                            channel?.basicPublish(
                                "",
                                id,
                                null,
                                jsonBody.toString().toByteArray(Charset.forName("utf-8"))
                            )
                        } catch (e: TimeoutException) {
                            liveError.postValue(e)
                            e.printStackTrace()
                        } catch (e: IOException) {
                            liveError.postValue(e)
                            e.printStackTrace()
                        } catch (e: URISyntaxException) {
                            liveError.postValue(e)
                            e.printStackTrace()
                        }
                    }
                    .doOnError {
                        liveError.observeForever {
                            error.invoke(it)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }
        }
    }

    companion object {
        private var id: String? = null
        fun setID(id: String) {
            this.id = id
        }

        fun getID() = id

        fun getInstance(): RabbitInstance? {
            return Rabbit(connection).setupInstance()
        }

        private var connection: Connection? = null
        private val liveError = MutableLiveData<Exception>()

        /**
         * ID is topic
         * */
        fun setInstance(id: String?, url: String, error: ((Exception) -> Unit)? = null) {
            this.id = id

            logi("rabbit setup instance")
            Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map {
                    val factory = ConnectionFactory()
                    factory.setUri(it)

                    try {
                        val connection = factory.newConnection()
                        logi("rabbit connection ready")
                        this.connection = connection
                        return@map connection
                    } catch (e: ConnectException) {
                        liveError.postValue(e)
                        loge(e.localizedMessage)
                        return@map null
                    } catch (e: Exception) {
                        liveError.postValue(e)
                        loge(e.localizedMessage)
                        return@map null
                    } catch (e: NullPointerException) {
                        liveError.postValue(e)
                        loge(e.localizedMessage)
                        return@map null
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    liveError.observeForever {
                        error?.invoke(it)
                    }
                }
                .subscribe()
        }
    }
}