package ru.korolevss.tribune.repository

import okhttp3.Interceptor
import okhttp3.Response

class InjectAuthTokenInterceptor(private val authToken: String): Interceptor {

    companion object {
        private const val AUTH_TOKEN_HEADER = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithToken = originalRequest.newBuilder()
            .header(AUTH_TOKEN_HEADER, "Bearer $authToken")
            .build()

        return chain.proceed(requestWithToken)
    }
}
