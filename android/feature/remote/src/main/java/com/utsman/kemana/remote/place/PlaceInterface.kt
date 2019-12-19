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

import com.utsman.kemana.base.REMOTE_URL
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PlaceInterface {

    // localhost:8800/api/v1/place/search?q=mon&from=-6.1767059,106.828464&radius=30000
    // localhost:8800/api/v1/place/direction?from=-6.1767059,106.828464&to=-6.247402,106.79111

    @GET("/api/v1/place/search")
    fun search(
        @Query("q") query: String,
        @Query("from") from: String
    ) : Flowable<PlacesResponses>

    @GET("/api/v1/place")
    fun searchAddress(
        @Query("from") from: String
    ) : Flowable<PlacesResponses>

    @GET("/api/v1/place/direction")
    fun getPolyline(
        @Query("from") from: String,
        @Query("to") to: String
    ) : Flowable<PolylineResponses>

    companion object {
        fun create(): PlaceInterface {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(REMOTE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

            return retrofit.create(PlaceInterface::class.java)
        }
    }
}