package com.jesse.batteria.service

import com.jesse.batteria.date.TwilioResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface TwilioService {
    @FormUrlEncoded
    @POST("2010-04-01/Accounts/{AccountSid}/Messages.json")
    fun sendSms(
        @Path("AccountSid") accountSid: String?,
        @Field("To") to: String?,
        @Field("From") from: String?,
        @Field("Body") body: String?,
        @Field("MediaUrl") mediaUrl: String?
    ): Call<TwilioResponse?>?
}