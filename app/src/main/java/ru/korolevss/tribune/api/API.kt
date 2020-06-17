package ru.korolevss.tribune.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.korolevss.tribune.dto.AttachmentModel
import ru.korolevss.tribune.dto.AuthRequestParams
import ru.korolevss.tribune.dto.PasswordChangeRequestDto
import ru.korolevss.tribune.dto.PostRequestDto
import ru.korolevss.tribune.model.PostModel
import ru.korolevss.tribune.model.Token
import ru.korolevss.tribune.model.UserModel

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

    @GET("api/v1/posts/me")
    suspend fun getPostsOfMe(): Response<List<PostModel>>

    @GET("api/v1/posts/username/{username}")
    suspend fun getPostsOfUser(@Path("username") username: String): Response<List<PostModel>>

    @Multipart
    @POST("api/v1/media")
    suspend fun uploadImage(@Part file: MultipartBody.Part):
            Response<AttachmentModel>

    @POST("api/v1/posts")
    suspend fun createPost(@Body postRequestDto: PostRequestDto): Response<Void>

    @GET("api/v1/me")
    suspend fun getMe(): Response<UserModel>

    @POST("api/v1/me/image")
    suspend fun addImageToUser(@Body attachmentModel: AttachmentModel): Response<Void>

    @POST("api/v1/me/change-password")
    suspend fun changePassword(@Body passwordChangeRequestDto: PasswordChangeRequestDto): Response<Token>
}