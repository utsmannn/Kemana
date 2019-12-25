package com.utsman.feature.remote.instance

import com.utsman.feature.base.REMOTE_URL
import com.utsman.feature.remote.model.UserDeletedResponses
import com.utsman.feature.remote.model.UserResponses
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface UserInstance {

    @POST("/api/v1/user/save")
    fun saveUser(
        @Query("document") document: String
    ): Observable<UserResponses>

    @PUT("/api/v1/user/edit")
    fun editUser(
        @Query("document") document: String,
        @Query("id") id: String
    ): Observable<UserResponses>

    @DELETE("/api/v1/user/delete")
    fun deleteUser(
        @Query("document") document: String,
        @Query("id") id: String
    ): Observable<UserDeletedResponses>

    companion object {
        fun create(): UserInstance {
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

            return retrofit.create(UserInstance::class.java)
        }
    }
}