package com.jesse.batteria.date

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class TwilioResponse(
    val sid: String,
    val status: String,
    val to: String,
    val body: String
)
