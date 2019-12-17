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

package com.utsman.kemana.remote.driver

import com.utsman.kemana.base.REMOTE_URL
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RemoteInstance {

    @POST("/api/v1/driver/")
    fun insertDriver(@Body driver: Driver): Flowable<Responses>

    @GET("/api/v1/driver/active")
    fun getAllDriver(): Flowable<Responses>

    @GET("/api/v1/driver/active/email")
    fun getAllDriverEmail(): Flowable<ResponsesEmail>

    @GET("/api/v1/driver/{id}")
    fun getDriver(@Path("id") id: String): Flowable<Responses>

    @DELETE("/api/v1/driver/{id}")
    fun deleteDriver(@Path("id") id: String): Flowable<Responses>

    @DELETE("/api/v1/driver")
    fun deleteDriverByEmail(
        @Query("email") email: String
    ): Flowable<Responses>

    @PUT("/api/v1/driver/{id}")
    fun editDriver(
        @Path("id") id: String,
        @Body position: Position
    ): Flowable<Responses>

    @PUT("/api/v1/driver")
    fun editDriverByEmail(
        @Query("email") email: String,
        @Body position: Position
    ): Flowable<Responses>


    // registered
    @POST("/api/v1/driver_db/")
    fun registerDriver(@Body driver: Driver): Flowable<Responses>

    @GET("/api/v1/driver_db/{id}")
    fun getRegisteredDriver(@Path("id") id: String?): Flowable<Responses>

    @GET("/api/v1/driver_db")
    fun getRegisteredDriverByEmail(@Query("email") email: String?): Flowable<Responses>

    @GET("/api/v1/driver_db/attr/{id}")
    fun getAttrRegisteredDriver(
        @Path("id") id: String?
    ): Flowable<ResponsesAttribute>

    @GET("/api/v1/driver_db/check/{email}")
    fun checkRegisteredDriver(
        @Path("email") email: String?
    ): Flowable<ResponsesChecking>

    @PUT("/api/v1/driver_db")
    fun editDriverRegisteredByEmail(
        @Query("email") email: String?,
        @Body position: Position?
    ): Flowable<Responses>

    companion object {

        fun create(): RemoteInstance {
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

            return retrofit.create(RemoteInstance::class.java)
        }

    }
}