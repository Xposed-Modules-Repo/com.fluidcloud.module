package com.fluidcloud.module.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TestNotificationService : Service() {

    companion object {
        private const val CHANNEL_LIVE = "fluidcloud_live"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            context.startService(Intent(context, TestNotificationService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TestNotificationService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val liveChannel = NotificationChannel(
                CHANNEL_LIVE, "实时活动",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(liveChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tapIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val progressStyle = NotificationCompat.ProgressStyle()
            .setProgressSegments(listOf(NotificationCompat.ProgressStyle.Segment(100)))
            .setStyledByProgress(true)
            .setProgress(50)

        val notification = NotificationCompat.Builder(this, CHANNEL_LIVE)
            .setContentTitle("流体云实时活动")
            .setContentText("测试通知")
            .setShortCriticalText("50%")
            .setSmallIcon(com.fluidcloud.module.R.drawable.ic_launcher_foreground)
            .setContentIntent(tapIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setStyle(progressStyle)
            .setRequestPromotedOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
