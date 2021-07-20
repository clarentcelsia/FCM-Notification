package com.example.firebasenotification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasenotification.API.Instance.Companion.api
import com.example.firebasenotification.API.NotificationResponse
import com.example.firebasenotification.FirebaseService.Companion.refreshedToken
import com.example.firebasenotification.FirebaseService.Companion.sharedPreferences
import com.example.firebasenotification.Model.Data
import com.example.firebasenotification.Model.Sender
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val to = "/message/subscriber"

    private lateinit var etMessage: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMessage = findViewById(R.id.etMessage)
        button = findViewById(R.id.btnSendNotification)

        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE)

        //the message will be sent to specific device (user)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this, "failed to get token!", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
            // get new registration token
            refreshedToken = it.result
        }

        //the message will be sent to those who subscribe the specific content/etc
        FirebaseMessaging.getInstance().subscribeToTopic(to)

        button.setOnClickListener {
            val message = etMessage.text.toString()
            val tokens = refreshedToken!!
            Sender(
                Data("New Message", message),
                tokens
            ).also {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(body: Sender) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val response = api.sendNotification(body)
            response.enqueue(object : Callback<NotificationResponse>{
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    Log.e("Success", "sendNotification: ${response.message()} ")
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Log.e("Error", "sendNotification: ${t.message.toString()} ")
                }
            })

        }catch (e: Exception){
            Log.e("Error", "sendNotification: ${e.message}")
        }

    }

}