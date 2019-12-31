package com.utsman.feature.remote.instance

import com.utsman.feature.remote.model.RequestServiceResponses
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface RequestServiceInstance {

    @POST("/service/start/{email}")
    fun requestToServer(@Path("email") email: String): Observable<RequestServiceResponses>

    companion object {
        fun create(): RequestServiceInstance {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.43.193:1010")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

            return retrofit.create(RequestServiceInstance::class.java)
        }
    }
}