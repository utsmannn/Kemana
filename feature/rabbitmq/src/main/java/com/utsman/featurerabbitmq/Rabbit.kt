package com.utsman.featurerabbitmq

import androidx.lifecycle.MutableLiveData
import com.rabbitmq.client.*
import com.utsman.kemana.base.loge
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Rabbit {

    companion object {
        private var url = ""
        internal var queueName = ""

        internal var channel: Channel? = null

        //internal val liveMsg = MutableLiveData<Message>()

        fun connect(url: String, queueName: String, composite: CompositeDisposable) {
            Rabbit.url = url
            Rabbit.queueName = queueName

            val disposableChannel = Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map {
                    val factory = ConnectionFactory()
                    val connection = factory.newConnection()
                    val channel = connection.createChannel()
                    channel.queueDeclare(queueName, false, false, false, null)
                    return@map channel
                }
                /*.doOnNext { ch ->
                    liveMsg.observeForever {
                        val qName = it.queueName
                        val msg = it.msg
                        ch.basicPublish("", qName, null, msg.toByteArray(Charset.forName("utf-8")))
                    }
                }*/
                .subscribe({
                    channel = it

                    /*it.basicConsume(queueName, true, object : DefaultConsumer(channel) {
                        override fun handleDelivery(
                            consumerTag: String?,
                            envelope: Envelope?,
                            properties: AMQP.BasicProperties?,
                            body: ByteArray
                        ) {
                            super.handleDelivery(consumerTag, envelope, properties, body)
                            val msg = String(body, Charset.forName("utf-8"))
                            println("message is -> $msg from $consumerTag, queue is --> $queueName")
                        }
                    })*/

                }, {
                    loge("failed --> ${it.localizedMessage}")
                    it.printStackTrace()
                })

            composite.add(disposableChannel)
        }

        fun publishTo(composite: CompositeDisposable, message: Message) {

            val disposablePub = Observable.just(channel)
                .subscribeOn(Schedulers.io())
                .doOnNext {  ch ->
                    val qName = message.queueName
                    val msg = message.msg
                    ch?.basicPublish("", qName, null, msg.toByteArray(Charset.forName("utf-8")))
                }
                .subscribe()

            composite.add(disposablePub)
        }

        fun subscribe(composite: CompositeDisposable, msg: (String) -> Unit) {
            val disposableSubs = Observable.just(channel)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    it?.basicConsume(queueName, true, object : DefaultConsumer(channel) {
                        override fun handleDelivery(
                            consumerTag: String?,
                            envelope: Envelope?,
                            properties: AMQP.BasicProperties?,
                            body: ByteArray
                        ) {
                            super.handleDelivery(consumerTag, envelope, properties, body)
                            val msgRec = String(body, Charset.forName("utf-8"))
                            msg.invoke(msgRec)
                            println("message is -> $msgRec from $consumerTag, queue is --> $queueName")
                        }
                    })
                }
                .subscribe()

            composite.add(disposableSubs)
        }
    }
}

data class Message(val queueName: String, val msg: String)