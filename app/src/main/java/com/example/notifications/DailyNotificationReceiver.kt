package com.example.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.WiltApplication
import kotlin.random.Random

class DailyNotificationReceiver : BroadcastReceiver() {

    companion object {
        val GAMER_MESSAGES = listOf(
            "OYE EL PROXY WILT ESTA FULL ⚡🎮",
            "OYE VAMOS POR UNAS VICTORIAS 🔥🏆",
            "AQUI ESPERANDO A ESA PERSONA QUE NUNCA JUEGA 🕹️😎",
            "PRONTO SERA LA NUEVA ACTUALIZACIÓN 🚀⚡"
        )

        fun triggerNotification(context: Context, customMessage: String? = null) {
            val message = customMessage ?: GAMER_MESSAGES[Random.nextInt(GAMER_MESSAGES.size)]

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1001,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, WiltApplication.CHANNEL_GAMER_REMINDERS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Wilt Gamin Pro ⚡")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.notify(Random.nextInt(1000, 9999), notification)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DailyNotificationScheduler.scheduleDailyReminder(context)
            return
        }

        triggerNotification(context)
        // Reschedule for next day
        DailyNotificationScheduler.scheduleDailyReminder(context)
    }
}
