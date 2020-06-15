package ru.korolevss.tribune.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.korolevss.tribune.dto.AuthRequestParams
import ru.korolevss.tribune.model.PostModel
import ru.korolevss.tribune.model.Token

interface API {
    @POST("api/v1/authentication")
    suspend fun authenticate(@Body authRequestParams: AuthRequestParams): Response<Token>

    @POST("api/v1/registration")
    suspend fun register(@Body authRequestParams: AuthRequestParams): Response<Token>

    @GET("api/v1/posts/{id}/before")
    suspend fun getPostsBefore(@Path("id") id: Long): Response<List<PostModel>>

    @POST("api/v1/firebase-token")
    suspend fun firebasePushToken(@Body token: Token): Response<Void>

    @GET("api/v1/posts/recent")
    suspend fun getRecent(): Response<List<PostModel>>

    @POST("api/v1/posts/{id}/like")
    suspend fun likedByUser(@Path("id") id: Long): Response<PostModel>

    @POST("api/v1/posts/{id}/dislike")
    suspend fun dislikedByUser(@Path("id") id: Long): Response<PostModel>

}