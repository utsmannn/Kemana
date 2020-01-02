package com.utsman.kemana.driver.services

import com.utsman.feature.base.BASE_URL
import com.utsman.feature.base.Preferences_getPort
import com.utsman.feature.base.RxService
import com.utsman.feature.base.logi
import com.utsman.feature.rabbitmq.Rabbit
import com.utsman.kemana.driver.subscriber.OnlineUpdater
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import org.json.JSONObject
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import kotlin.concurrent.thread

class BidService : RxService() {

    private var isOnline = false

    override fun onCreate() {
        super.onCreate()

        Notify.listen(OnlineUpdater::class.java, NotifyProvider(), Consumer {
            logi("anjayy --> from bid service")
            isOnline = it.isOnline

        })

        // val url = "192.168.1.13"
        //
        //        thread {
        //            val socket = Socket(url, 8080)
        //            val o = ObjectOutputStream(socket.getOutputStream())
        //            Log.i("aaaaaa", "sending")
        //            o.writeObject("oke")
        //        }


        val port = Preferences_getPort()

        thread {
            val socket = Socket(BASE_URL, port)
            val o = ObjectOutputStream(socket.getOutputStream())
            val i = ObjectInputStream(socket.getInputStream())
            logi("sending to server")
            o.writeObject("oke")

            val msg = i.readObject() as String
            logi("aaaa -> $msg")
        }

        Rabbit.getInstance()?.listen { from, body ->
            logi("anjay from $from --> $body")
        }

        /*Rabbit.getInstance()?.listen { from, body ->
            when (body.getString("type")) {
                "checking" -> {
                    val data = body.getJSONObject("data")
                    val nowTime = System.currentTimeMillis()
                    val sendingTime = data.getLong("time")
                    val diffTime = nowTime-sendingTime
                    val desc = "sending at: $sendingTime -- receive at: $nowTime, diff: $diffTime"

                    logi("anjay order from --> $from body is --> $data, time -> $desc")

                    if (diffTime <= 10000) {
                        val json = JSONObject()
                        val jsonData = JSONObject()
                        jsonData.put("online", true)
                        json.put("type", "checking")
                        json.put("data", jsonData)

                        Rabbit.getInstance()?.publishTo(from, json) {

                        }
                    }
                }
            }
        }*/
    }
}