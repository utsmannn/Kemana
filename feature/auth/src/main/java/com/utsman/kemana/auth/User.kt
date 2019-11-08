/*
 * Copyright 2019 Muhammad Utsman
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

package com.utsman.kemana.auth

import com.google.gson.annotations.SerializedName

data class User(val userId: String,
                val name: String,
                val email: String,
                val password: String? = null,
                val vehiclesType: String? = "passenger",
                val vehiclesPlat: String? = "passenger",
                val photoProfile: String? = "https://usa-latestnews.com/wp-content/plugins/all-in-one-seo-pack/images/default-user-image.png",
                val objectId: String? = null,
                @SerializedName("user-token")
                var token: String? = null,
                var lat: Double? = 0.0,
                var lon: Double? = 0.0,
                var angle: Double? = 0.0,
                var onOrder: Boolean = false)