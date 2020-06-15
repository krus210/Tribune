package ru.korolevss.tribune.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.korolevss.tribune.api.API
import ru.korolevss.tribune.dto.AuthRequestParams
import ru.korolevss.tribune.model.Token

object Repository {

    const val BASE_URL = "https://tribune-api.herokuapp.com/"

    private var retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    private var api: API = retrofit.create(API::class.java)

    suspend fun authenticate(name: String, password: String) =
        api.authenticate(AuthRequestParams(name, password))

    suspend fun register(name: String, password: String) =
        api.register(AuthRequestParams(name, password))

    fun createRetrofitWithAuth(authToken: String) {
        val httpLoggerInterceptor = HttpLoggingInterceptor()
        httpLoggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(InjectAuthTokenInterceptor(authToken))
            .addInterceptor(httpLoggerInterceptor)
            .build()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(API::class.java)
    }

    suspend fun getPostsBefore(id: Long) = api.getPostsBefore(id)

    suspend fun firebasePushToken(token: Token): Response<Void> = api.firebasePushToken(token)

    suspend fun getRecent() = api.getRecent()

    suspend fun likedByUser(id: Long) = api.likedByUser(id)

    suspend fun dislikedByUser(id: Long) = api.dislikedByUser(id)

}