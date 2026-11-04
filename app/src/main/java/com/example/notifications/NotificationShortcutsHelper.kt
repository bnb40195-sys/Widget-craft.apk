package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppShortcut

object NotificationShortcutsHelper {

    const val NOTIFICATION_ID = 5050
    const val CHANNEL_ID = "notification_shortcuts_channel"

    fun showOrUpdateNotificationShortcuts(
        context: Context,
        shortcuts: List<AppShortcut>,
        pinnedIds: Set<String>? = null
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "إختصارات التطبيقات في مركز الإشعارات",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "شريط وصول سريع لتشغيل التطبيقات المفضلة من مركز الإشعارات أوفلاين"
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap notification body to open app
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_art)
            .setContentTitle("مركز إختصارات التطبيقات ⚡")
            .setContentText("اضغط لتشغيل أي تطبيق مباشرة من شريط الإشعارات")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(mainPendingIntent)

        // Filter and choose up to 5 shortcuts
        val candidates = if (pinnedIds != null && pinnedIds.isNotEmpty()) {
            shortcuts.filter { pinnedIds.contains(it.id) && !it.isHidden }
        } else {
            shortcuts.filter { !it.isHidden }
        }

        val displayList = if (candidates.isNotEmpty()) candidates.take(5) else shortcuts.take(5)

        displayList.forEachIndexed { index, shortcut ->
            val launchIntent = Intent(context, ShortcutLaunchReceiver::class.java).apply {
                action = ShortcutLaunchReceiver.ACTION_LAUNCH
                putExtra(ShortcutLaunchReceiver.EXTRA_PACKAGE_NAME, shortcut.packageName)
                putExtra(ShortcutLaunchReceiver.EXTRA_SYMBOL_ACTION, shortcut.iconSymbol)
                putExtra(ShortcutLaunchReceiver.EXTRA_LABEL, shortcut.displayLabel)
            }

            val actionPendingIntent = PendingIntent.getBroadcast(
                context,
                shortcut.id.hashCode() + index,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val actionIcon = when (shortcut.iconSymbol) {
                "Phone" -> android.R.drawable.ic_menu_call
                "Camera" -> android.R.drawable.ic_menu_camera
                "Chat" -> android.R.drawable.ic_menu_send
                "Browser" -> android.R.drawable.ic_menu_view
                "Tools" -> android.R.drawable.ic_menu_preferences
                else -> android.R.drawable.ic_menu_agenda
            }

            val actionTitle = when (shortcut.iconSymbol) {
                "Phone" -> "📞 ${shortcut.displayLabel}"
                "Camera" -> "📷 ${shortcut.displayLabel}"
                "Chat" -> "💬 ${shortcut.displayLabel}"
                "Browser" -> "🌐 ${shortcut.displayLabel}"
                "Tools" -> "⚙️ ${shortcut.displayLabel}"
                "Notes" -> "📝 ${shortcut.displayLabel}"
                "Music" -> "🎵 ${shortcut.displayLabel}"
                else -> "⚡ ${shortcut.displayLabel}"
            }

            builder.addAction(
                actionIcon,
                actionTitle,
                actionPendingIntent
            )
        }

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: Exception) {
            // Permission or security exception fallback
        }
    }

    fun cancelNotificationShortcuts(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
