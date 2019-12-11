package com.utsman.kemana.remote.place

import com.utsman.kemana.base.REMOTE_URL
import com.utsman.kemana.remote.RemoteInstance
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceInterface {

    // localhost:8800/api/v1/place/search?q=mon&from=-6.1767059,106.828464&radius=30000

    @GET("/api/v1/place/search")
    fun search(
        @Query("q") query: String,
        @Query("from") from: String,
        @Query("radius") radius: Int
    ) : Flowable<PlacesResponses>

    @GET("/api/v1/place")
    fun searchAddress(
        @Query("from") from: String
    ) : Flowable<PlacesResponses>

    companion object {
        fun create(): PlaceInterface {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
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