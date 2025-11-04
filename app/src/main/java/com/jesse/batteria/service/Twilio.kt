package com.jesse.batteria.service

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TwilioClient {

    fun createService(accountSid: String, authToken: String): TwilioService {
        val credentials = Credentials.basic(accountSid, authToken)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", credentials)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twilio.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TwilioService::class.java)
    }
}
