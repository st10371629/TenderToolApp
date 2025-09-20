package com.tendertool.app.src

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit
{
    private const val URL = "https://4owkixd548.execute-api.us-east-1.amazonaws.com/dev/"

    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } //logs body content

    private val client = OkHttpClient.Builder().addInterceptor(logging).build() //adds the logging interceptor to the client

    val api: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}