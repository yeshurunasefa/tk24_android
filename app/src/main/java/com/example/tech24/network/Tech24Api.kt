package com.example.tech24.network

import com.example.tech24.model.LoginRequest
import com.example.tech24.model.LoginResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.tech24.model.CallEntriesResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.tech24et.com/api/"

private val logger = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Tech24ApiService {

    @GET("callentries")
    suspend fun getCallEntries(
        @Header("Authorization")
        bearer: String,
        @Query("page")
        page: Int
    ): Response<CallEntriesResponse>

    @POST("login-with-device")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("logout-with-device")
    suspend fun logout(
        @Header("Authorization")
        bearer: String
    ): Response<Unit>
}

object Tech24Api {

    val service: Tech24ApiService by lazy {
        retrofit.create(Tech24ApiService::class.java)
    }

}