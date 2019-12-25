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

@file:Suppress("SpellCheckingInspection", "MayBeConstant")

package com.utsman.feature.base

import android.annotation.SuppressLint

val MAPBOX_TOKEN = "sk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazNrMXAxdGcwY2pmM25xeHE1NDJidHA3In0.a61JfIKzUdMWaCNhCLbtxg"
val HERE_API_KEY = "EKZhNIBtjrjeYxqdyhCMQ1kxVc_O4QGfxEJLqWt0Hp0"

val REMOTE_URL = "http://192.168.43.193:8080"
//val REMOTE_URL = "http://10.1.3.65:8080"
//val REMOTE_URL = "http://192.168.1.28:8080"
//val REMOTE_URL = "http://localhost:8080"

@SuppressLint("AuthLeak")
val RABBIT_URL = "amqp://user1:1234@192.168.43.193/%2F"
//val RABBIT_URL = "amqp://edafafqh:ypJNO0725gpmo1tFnr4cbyFThZ1ZwMLH@woodpecker.rmq.cloudamqp.com/edafafqh"
//val RABBIT_URL = "amqp://user1:1234@10.1.3.65/%2F"
//val RABBIT_URL = "amqp://user1:1234@192.168.1.28/%2F"