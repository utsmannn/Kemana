package com.utsman.kemana.auth

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface AuthInstance {

    @Headers("Content-Type: application/json")
    @POST("{app-id}/{rest-key}/users/register")
    fun register(
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Body user: User
    ): Call<User>

    @Headers("Content-Type: application/json")
    @POST("{app-id}/{rest-key}/users/login")
    fun login(
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Body userLogin: UserLogin
    ): Call<User>

    companion object {
        fun create(): AuthInstance {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

            val retrofit =  Retrofit.Builder()
                .baseUrl("https://api.backendless.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(AuthInstance::class.java)
        }
    }

}