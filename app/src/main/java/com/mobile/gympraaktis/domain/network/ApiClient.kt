package com.mobile.gympraaktis.domain.network

import com.mobile.gympraaktis.BuildConfig
import com.mobile.gympraaktis.domain.model.FeedbackModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ApiClient {

    private val client by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.praaktis.com")
            .client(
                OkHttpClient.Builder().apply {
                    if (BuildConfig.DEBUG)
                        addInterceptor(
                            HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                        )
                }.build()
            )
            .build()
    }

    val service: ApiInterface by lazy {
        client.create(ApiInterface::class.java)
    }

}

interface ApiInterface {

    @POST("/api/storeFeedback/")
    suspend fun storeFeedback(@Body feedbackModel: FeedbackModel): Response<Any>

}