package com.utsman.featurerabbitmq

import androidx.lifecycle.MutableLiveData
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.utsman.kemana.base.logi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.nio.charset.Charset

@Suppress("UNCHECKED_CAST")
class Rabbit private constructor(private val url: String) {

    private val liveMsg = MutableLiveData<JSONObject>()

    internal val rabbitInstance = object : RabbitInstance {

        override fun listen(msg: (from: String, JSONObject) -> Unit): Disposable {
            return Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map {
                    val factory = ConnectionFactory()
                    factory.setUri(it)
                    return@map factory
                }
                .doOnNext {
                    it.setUri(url)
                    val connection = it.newConnection()
                    val channel = connection.createChannel()

                    channel.queueDeclare(id, false, false, false, null)

                    channel.exchangeDeclare("kemana", "fanout")
                    channel.queueBind(id, "kemana", id)

                    channel.basicConsume(id, true, object : DefaultConsumer(channel) {
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
                .subscribe({
                    liveMsg.observeForever {
                        val id = it.getString("id")
                        val msgBody = it.getJSONObject("body")
                        logi("data string is -> $msgBody")

                        msg.invoke(id, msgBody)
                    }
                }, {
                    it.printStackTrace()
                })
        }

        override fun publishTo(id: String, msg: JSONObject): Disposable {
            return Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map {
                    val factory = ConnectionFactory()
                    factory.setUri(it)

                    return@map factory
                }
                .doOnNext {
                    it.setUri(url)
                    val connection = it.newConnection()
                    val channel = connection.createChannel()

                    channel.queueDeclare(id, false, false, false, null)

                    channel.exchangeDeclare("kemana", "fanout")
                    channel.queueBind(id, "kemana", id)

                    val jsonBody = JSONObject()
                    jsonBody.put("id", Rabbit.id)
                    jsonBody.put("body", msg)

                    channel.basicPublish("", id, null, jsonBody.toString().toByteArray(Charset.forName("utf-8")))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    companion object {
        private var id: String = ""

        /**
         * ID is topic
         * */
        fun setID(id: String) {
            this.id = id
        }

        fun getID() = id

        fun fromUrl(url: String): RabbitInstance {
            return Rabbit(url).rabbitInstance
        }
    }

}