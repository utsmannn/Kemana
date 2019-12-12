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

    @GET("/api/v1/here/search")
    fun search(
        @Query("q") query: String,
        @Query("from") from: String,
        @Query("radius") radius: Int
    ) : Flowable<PlacesResponses>

    @GET("/api/v1/place")
    fun searchAddress(
        @Query("from") from: String
    ) : Flowable<PlacesResponses>

    @POST("/api/v1/place/direction")
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