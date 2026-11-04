package com.example.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class WidgetAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertTitle = intent.getStringExtra("EXTRA_ALERT_TITLE") ?: "تنبيه WidgetCraft الذكي"
        val alertId = intent.getStringExtra("EXTRA_ALERT_ID") ?: "default_id"
        val vibrate = intent.getBooleanExtra("EXTRA_VIBRATE", true)

        val channelId = "widget_alerts_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تنبيهات وتذكيرات الويدجات أوفلاين",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات مواعيد وتذكيرات مهام الويدجات أوفلاين"
                enableVibration(vibrate)
            }
            notificationManager.createNotificationChannel(channel)
        }

        if (vibrate) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 300, 150, 300), -1)
                }
            } catch (e: Exception) {
                // fallback
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_icon_art)
            .setContentTitle("تنبيه الويدجت ⏰")
            .setContentText(alertTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(alertId.hashCode(), notification)
    }
}
