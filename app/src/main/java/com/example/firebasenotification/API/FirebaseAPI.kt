package com.example.firebasenotification.API

import com.example.firebasenotification.Common.Companion.CONTENT_TYPE
import com.example.firebasenotification.Common.Companion.KEY
import com.example.firebasenotification.Model.Sender
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseAPI {

    @Headers(
        "Content-Type:${CONTENT_TYPE}",
        "Authorization:key=${KEY}"
    )

    @POST("fcm/send")
    suspend fun sendNotification(@Body body: Sender): Call<NotificationResponse>
}