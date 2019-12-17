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

package com.utsman.kemana.remote.place

import com.google.gson.annotations.SerializedName

data class Places(
    val id: String?,
    val placeName: String?,
    val addressName: String?,
    val geometry: List<Double?>?,
    @SerializedName("geometry_draw_url")
    val geometryDrawUrl: String?
)

data class PlacesResponses(
    val size: Int?,
    val places: List<Places?>?
)

data class PolylineResponses(
    val distance: Double?,
    val geometry: String?
)