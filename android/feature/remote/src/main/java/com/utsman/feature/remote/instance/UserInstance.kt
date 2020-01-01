package com.utsman.feature.remote.instance

import com.utsman.feature.base.REMOTE_URL
import com.utsman.feature.remote.model.User
import com.utsman.feature.remote.model.UserDeletedResponses
import com.utsman.feature.remote.model.UserResponses
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface UserInstance {

    @POST("/{email}/api/v1/user/save")
    fun saveUser(
        @Path("email") email: String?,
        @Query("document") document: String,
        @Body user: User
    ): Observable<UserResponses>

    @GET("/{email}/api/v1/user")
    fun getUser(
        @Path("email") email: String?,
        @Query("id") id: String?,
        @Query("document") document: String
    ): Observable<UserResponses>

    @PUT("/{email}/api/v1/user/edit")
    fun editUser(
        @Path("email") email: String?,
        @Query("document") document: String,
        @Query("id") id: String
    ): Observable<UserResponses>

    @DELETE("/{email}/api/v1/user/delete")
    fun deleteUser(
        @Path("email") email: String?,
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