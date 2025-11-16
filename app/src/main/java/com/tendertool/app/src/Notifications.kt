package com.tendertool.app.src

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tendertool.app.MainActivity
import com.tendertool.app.R

object Notifications {
    const val CHANNEL_ID = "tender_watch_channel"
    const val CHANNEL_NAME = "Tender Watch"
    const val CHANNEL_DESC = "Notifications for tenders in watchlist"

    fun createChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
                .apply { description = CHANNEL_DESC }

            val nm = context.getSystemService((Context.NOTIFICATION_SERVICE)) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification(context: Context, id: Int, title: String, body: String){
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingFlag = when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingFlag)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)){
            notify(id, builder.build())
        }
    }
}