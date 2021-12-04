package com.example.cappsule

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationBroadcast : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            val notifyIntent = Intent(p0, MainActivity::class.java).apply {
            }
            val notifyPendingIntent = PendingIntent.getActivity(
                p0, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder = NotificationCompat.Builder(p0, "dailyNotifier")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(p0.getString(R.string.NotifTitle))
                .setContentText(p0.getString(R.string.NotifBody))
                .setContentIntent(notifyPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(p0)
            notificationManager.notify(200, builder.build())
        }
    }
}