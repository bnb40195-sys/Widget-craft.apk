package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast

class ShortcutLaunchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val externalUrl = intent.getStringExtra(EXTRA_EXTERNAL_URL)
        val symbolAction = intent.getStringExtra(EXTRA_SYMBOL_ACTION)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "التطبيق"

        var launchIntent: Intent? = null

        // Try external URL first if provided
        if (!externalUrl.isNullOrBlank()) {
            try {
                launchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl))
            } catch (e: Exception) {
                // fallback
            }
        }

        // Try package name if no URL or URL failed
        if (launchIntent == null && !packageName.isNullOrBlank()) {
            launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        }

        // Fallback to standard intents based on symbolAction
        if (launchIntent == null && !symbolAction.isNullOrBlank()) {
            launchIntent = when (symbolAction) {
                "Phone" -> Intent(Intent.ACTION_DIAL)
                "Chat" -> Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)
                "Camera" -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                "Browser" -> Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
                "Notes" -> Intent(Intent.ACTION_CREATE_NOTE)
                "Tools" -> Intent(android.provider.Settings.ACTION_SETTINGS)
                else -> null
            }
        }

        // Fallback to MainActivity
        if (launchIntent == null) {
            launchIntent = Intent(context, com.example.MainActivity::class.java)
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        try {
            context.startActivity(launchIntent)
            Toast.makeText(context, "تشغيل: $label 🚀", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // If failed, open main app
            val fallback = Intent(context, com.example.MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }

    companion object {
        const val ACTION_LAUNCH = "com.example.ACTION_LAUNCH_SHORTCUT"
        const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        const val EXTRA_EXTERNAL_URL = "EXTRA_EXTERNAL_URL"
        const val EXTRA_SYMBOL_ACTION = "EXTRA_SYMBOL_ACTION"
        const val EXTRA_LABEL = "EXTRA_LABEL"
    }
}
