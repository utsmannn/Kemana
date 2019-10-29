package com.utsman.kemana.backendless

import com.utsman.kemana.auth.User
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface BackendlessInstance {

    @POST("{app-id}/{rest-key}/data/{table}")
    fun saveUserToTable(
        @Header("Content-Type") contentType: String,
        @Header("user-token") token: String,
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("table") tableUser: String,
        @Body user: User
    ): Flowable<User>

    @POST("{app-id}/{rest-key}/data/driver_active")
    fun saveDriveActive(
        @Header("Content-Type") contentType: String,
        @Header("user-token") token: String,
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Body user: User
    ): Flowable<User>

    @PUT("{app-id}/{rest-key}/data/{table}/{object-id}")
    fun updateDriveLocation(
        @Header("Content-Type") contentType: String,
        @Header("user-token") token: String,
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("table") table: String,
        @Path("object-id") objectId: String,
        @Body user: User
    ): Flowable<User>

    @PUT("{app-id}/{rest-key}/data/driver_list/{object-id}")
    fun updateDriveLocationInList(
        @Header("Content-Type") contentType: String,
        @Header("user-token") token: String,
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("object-id") objectId: String,
        @Body user: User
    ): Flowable<User>

    @DELETE("{app-id}/{rest-key}/data/{table}/{object-id}")
    fun deleteDriveActive(
        @Header("Content-Type") contentType: String,
        @Header("user-token") token: String,
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("object-id") objectId: String,
        @Path("table") table: String
    ): Flowable<JSONObject>

    @GET("{app-id}/{rest-key}/data/{table}")
    fun getDriverList(
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("table") table: String
    ): Flowable<List<User>?>

    @GET("{app-id}/{rest-key}/data/{table}/{object-id}")
    fun getDriverById(
        @Path("app-id") appId: String,
        @Path("rest-key") restKey: String,
        @Path("object-id") userId: String,
        @Path("table") table: String
    ): Flowable<User>

    companion object{
        fun create(): BackendlessInstance {
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

            return retrofit.create(BackendlessInstance::class.java)
        }
    }
}