package com.utsman.feature.remote.instance

import com.utsman.feature.base.REMOTE_URL
import com.utsman.feature.remote.model.PlaceResponses
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PlaceInstance {

    @GET("/api/v1/place/search")
    fun searchPlace(
        @Query("q") query: String,
        @Query("from") from: List<Double>?,
        @Query("apikey") key: String
    ): Observable<PlaceResponses>

    @GET("/api/v1/place")
    fun getCurrentPlace(
        @Query("from") from: List<Double>?,
        @Query("apikey") key: String
    ): Observable<PlaceResponses>

    companion object {
        fun create(): PlaceInstance {
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

            return retrofit.create(PlaceInstance::class.java)
        }
    }
}