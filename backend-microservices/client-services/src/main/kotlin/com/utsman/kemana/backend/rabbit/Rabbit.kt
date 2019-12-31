package com.utsman.kemana.backend.rabbit

import com.rabbitmq.client.*
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException

class Rabbit private constructor(private var connection: Connection?) {

    private fun setupInstance() : RabbitInstance {
        return object : RabbitInstance {
            override fun listen(msg: (from: String, body: JSONObject) -> Unit) {
                val channel = connection?.createChannel()
                channel?.queueDeclare(id, false, false, true, null)

                println("rabbit channel created -> ${channel?.channelNumber}")
                channel?.exchangeDeclare("kemana-3", "fanout")
                channel?.queueBind(id, "kemana-3", id)

                channel?.basicConsume(id, true, object : DefaultConsumer(channel) {
                    override fun handleDelivery(
                            consumerTag: String?,
                            envelope: Envelope?,
                            properties: AMQP.BasicProperties?,
                            body: ByteArray
                    ) {
                        super.handleDelivery(consumerTag, envelope, properties, body)
                        val delivery = JSONObject(String(body, Charset.forName("utf-8")))
                        println(delivery.toString())
                        val id = delivery.getString("id")
                        msg.invoke(id, delivery)
                    }
                })
            }

            override fun publishTo(id: String, msg: JSONObject, error: ((java.lang.Exception) -> Unit)?) {
                val channel = connection?.createChannel()
                channel?.queueDeclare(id, false, false, false, null)
                try {
                    channel?.exchangeDeclare("kemana-3", "fanout")
                    channel?.queueBind(id, "kemana-3", id)

                    val jsonBody = JSONObject()
                    jsonBody.put("id", Companion.id)
                    jsonBody.put("body", msg)

                    channel?.basicPublish("", id, null, jsonBody.toString().toByteArray(Charset.forName("utf-8"))).apply {
                        channel?.close()
                    }
                } catch (e: TimeoutException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private var id: String? = null
        fun getID() = id

        fun getInstance(): RabbitInstance? {
            return Rabbit(connection).setupInstance()
        }

        private var connection: Connection? = null

        /**
         * ID is topic
         * */
        fun setInstance(id: String?, url: String, error: ((Exception) -> Unit)? = null) {
            Companion.id = id

            val factory = ConnectionFactory()
            factory.setUri(url)

            val connection = factory.newConnection()
            println("rabbit connection ready")
            Companion.connection = connection
        }
    }
}