@file:Suppress("SpellCheckingInspection", "MayBeConstant")

package com.utsman.kemana.base

import android.annotation.SuppressLint

val MAPKEY = "sk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazNrMXAxdGcwY2pmM25xeHE1NDJidHA3In0.a61JfIKzUdMWaCNhCLbtxg"
val REMOTE_URL = "http://192.168.43.193:8800"
//val REMOTE_URL = "http://10.1.3.65:8800"
//val REMOTE_URL = "http://192.168.1.28:8800"
//val REMOTE_URL = "http://localhost:8800"

@SuppressLint("AuthLeak")
val RABBIT_URL = "amqp://user1:1234@192.168.43.193/%2F"
//val RABBIT_URL = "amqp://user1:1234@192.168.1.28/%2F"