package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.notifications.DailyNotificationScheduler

class WiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        DailyNotificationScheduler.scheduleDailyReminder(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Channel for VPN Foreground Service
            val vpnChannel = NotificationChannel(
                CHANNEL_VPN_STATUS,
                "Wilt Gamin VPN Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el estado activo de la conexión VPN y estadísticas"
            }

            // Channel for Daily Gamer Reminders
            val gamerChannel = NotificationChannel(
                CHANNEL_GAMER_REMINDERS,
                "Wilt Gamin Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios diarios y avisos del servidor Wilt"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(vpnChannel)
            notificationManager.createNotificationChannel(gamerChannel)
        }
    }

    companion object {
        const val CHANNEL_VPN_STATUS = "wilt_vpn_channel"
        const val CHANNEL_GAMER_REMINDERS = "wilt_gamer_reminders_channel"
    }
}
