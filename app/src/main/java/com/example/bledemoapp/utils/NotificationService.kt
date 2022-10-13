package com.example.bledemoapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.bledemoapp.MainActivity
import com.example.bledemoapp.R


object NotificationService {
    fun pushNotification(context: Context) {
        val notificationId = 999

        val notificationManager =
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel("BleDemoApp", "BleDemoApp", NotificationManager.IMPORTANCE_HIGH)
            // Configure the notification channel.
            // Configure the notification channel.
            mChannel.description = "NotificationBLE"

            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED

            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            notificationManager.createNotificationChannel(mChannel)
        }
        val b = NotificationCompat.Builder(context, "BleDemoApp")

        b.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setTicker("BleDemoApp")
            .setContentText("Device scanned data inserted and uploaded successfully.")
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Device scanned data inserted and uploaded successfully."))
        notificationManager.notify(notificationId, b.build())
    }
}