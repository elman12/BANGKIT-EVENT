package com.elmansidik.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.elmansidik.dicodingevent.R

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Mengambil data dari inputData dengan nilai default untuk menghindari null
        val eventName = inputData.getString("event_name") ?: return Result.failure()
        val eventTime = inputData.getString("event_time") ?: return Result.failure()

        showNotification(eventName, eventTime)
        return Result.success()
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = createNotificationBuilder(eventName, eventTime)

        // Membuat Notification Channel jika SDK >= Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel untuk pengingat harian acara"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationBuilder(eventName: String, eventTime: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(eventName)
            .setContentText("Acara dimulai pada: $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
    }

    companion object {
        const val CHANNEL_ID = "daily_reminder_channel"
        const val CHANNEL_NAME = "Daily Reminder"
        const val NOTIFICATION_ID = 1
    }
}
