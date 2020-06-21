package ru.korolevss.tribune.repository

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.korolevss.tribune.api.API
import ru.korolevss.tribune.dto.AttachmentModel
import ru.korolevss.tribune.dto.AuthRequestParams
import ru.korolevss.tribune.dto.PasswordChangeRequestDto
import ru.korolevss.tribune.dto.PostRequestDto
import ru.korolevss.tribune.model.Token
import java.io.ByteArrayOutputStream

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

    suspend fun getPostsOfMe() = api.getPostsOfMe()

    suspend fun getPostsOfUser(username: String) = api.getPostsOfUser(username)

    suspend fun upload(bitmap: Bitmap): Response<AttachmentModel> {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFIle =
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), bos.toByteArray())
        val body =
            MultipartBody.Part.createFormData("file", "image.jpg", reqFIle)
        return api.uploadImage(body)
    }

    suspend fun createPost(
        content: String,
        attachmentImage: String,
        attachmentLink: String
    ): Response<Void> {
        var link: String? = attachmentLink
        if (link!!.isEmpty()) {
            link = null
        } else if (!link.contains("http://") || !link.contains("https://")) {
            link = "https://$link"
        }
        val postRequestDto = PostRequestDto(
            text = content,
            attachmentImage = attachmentImage,
            attachmentLink = link
        )
        return api.createPost(postRequestDto)
    }

    suspend fun getMe() = api.getMe()

    suspend fun addImageToUser(attachmentModel: AttachmentModel) =
        api.addImageToUser(attachmentModel)

    suspend fun changePassword(passwordChangeRequestDto: PasswordChangeRequestDto) =
        api.changePassword(passwordChangeRequestDto)

    suspend fun getLikeDislikeUsers(postId: Long) = api.getLikeDislikeUsers(postId)

}