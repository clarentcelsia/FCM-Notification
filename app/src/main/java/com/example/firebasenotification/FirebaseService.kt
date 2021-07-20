package com.example.firebasenotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    val TAG = "FirebaseService"

    companion object{
        var sharedPreferences: SharedPreferences? = null
        var refreshedToken: String?
            get(){ return sharedPreferences?.getString("token", "") }
            set(value){
            sharedPreferences?.edit()?.putString("token", value)?.apply() //apply -> async in sharedPref
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        //what to do when token updated
        refreshedToken = newToken
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d(TAG, "sent by: ${p0.from} ")

        showNotification(
            p0.data["title"],
            p0.data["body"]
        )

    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notificationID = Random.nextInt() //it's not should be overwritten
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //Build notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = getString(R.string.my_notification_channel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            //Configure the notif channel
            notificationChannel.apply {
                description = "This is channel description"
                enableLights(true)
                lightColor = Color.RED
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSound(ringtone)
            .setContentTitle(title)
            .setContentText(body)
            .setContentInfo("Info")
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationID, notificationBuilder.build())

    }
}
