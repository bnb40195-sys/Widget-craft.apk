package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetType
import com.example.device.DeviceStatsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(context)
            val activeEntity = db.widgetDao().getActiveLauncherWidget()
            val widgetConfig = activeEntity?.toDomain()

            for (appWidgetId in appWidgetIds) {
                val views = buildRemoteViews(context, widgetConfig)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_WIDGET_INCREMENT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val activeEntity = db.widgetDao().getActiveLauncherWidget()
                    if (activeEntity != null) {
                        db.widgetDao().incrementCounter(activeEntity.id)
                        updateAllWidgets(context)
                    }
                }
            }
            ACTION_WIDGET_REFRESH -> {
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_INCREMENT = "com.example.ACTION_WIDGET_INCREMENT"
        const val ACTION_WIDGET_REFRESH = "com.example.ACTION_WIDGET_REFRESH"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, CustomAppWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(provider)
            if (ids.isNotEmpty()) {
                val intent = Intent(context, CustomAppWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }

        fun pinWidgetToHomeScreen(context: Context, config: WidgetConfig): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val provider = ComponentName(context, CustomAppWidgetProvider::class.java)
                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    val callbackIntent = Intent(context, CustomAppWidgetProvider::class.java)
                    val successCallback = PendingIntent.getBroadcast(
                        context,
                        0,
                        callbackIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    appWidgetManager.requestPinAppWidget(provider, null, successCallback)
                    return true
                }
            }
            return false
        }

        fun buildRemoteViews(context: Context, config: WidgetConfig?): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_home_layout)
            val stats = DeviceStatsManager.getDeviceStats(context)

            if (config == null) {
                views.setTextViewText(R.id.widget_title, "WidgetCraft")
                views.setTextViewText(R.id.widget_badge, "بدون نت")
                views.setTextViewText(R.id.widget_main_value, stats.currentTimeFormatted)
                views.setTextViewText(R.id.widget_sub_value, "${stats.currentDayNameAr}، ${stats.currentDateAr}")
                views.setViewVisibility(R.id.widget_action_row, View.GONE)
            } else {
                views.setTextViewText(R.id.widget_title, config.name)
                views.setTextViewText(R.id.widget_badge, "بدون نت 100%")

                when (config.type) {
                    WidgetType.DIGITAL_CLOCK -> {
                        views.setTextViewText(R.id.widget_main_value, stats.currentTimeFormatted)
                        val batteryInfo = if (config.showBattery) " • ⚡ ${stats.batteryPct}%" else ""
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "${stats.currentDayNameAr}، ${stats.currentDateAr}$batteryInfo"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.BATTERY_DEVICE -> {
                        val chargingStr = if (stats.isCharging) " (قيد الشحن ⚡)" else ""
                        views.setTextViewText(R.id.widget_main_value, "${stats.batteryPct}%$chargingStr")
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "حالة البطارية: ${stats.batteryHealth} | المساحة الحرة: ${stats.availableStorageGb} GB"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.QUICK_NOTES -> {
                        views.setTextViewText(R.id.widget_main_value, "مفكرتي السريعة")
                        views.setTextViewText(R.id.widget_sub_value, config.noteContent)
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.DAILY_QUOTE -> {
                        views.setTextViewText(R.id.widget_main_value, "«${config.quoteArabic}»")
                        views.setTextViewText(R.id.widget_sub_value, "— ${config.quoteAuthor}")
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.HABIT_COUNTER -> {
                        views.setTextViewText(R.id.widget_main_value, "${config.counterValue}")
                        views.setTextViewText(R.id.widget_sub_value, config.counterLabel)
                        views.setViewVisibility(R.id.widget_action_row, View.VISIBLE)
                        views.setTextViewText(R.id.widget_btn_action, "+${config.counterStep} زيادة")

                        val incIntent = Intent(context, CustomAppWidgetProvider::class.java).apply {
                            action = ACTION_WIDGET_INCREMENT
                        }
                        val incPendingIntent = PendingIntent.getBroadcast(
                            context,
                            101,
                            incIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_btn_action, incPendingIntent)
                    }
                    WidgetType.ANALOG_CLOCK -> {
                        views.setTextViewText(R.id.widget_title, config.name)
                        views.setTextViewText(R.id.widget_badge, "ALG • عقارب")
                        views.setTextViewText(R.id.widget_main_value, stats.currentTimeFormatted)
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "ساعة عقارب طراز الجزائر • ${stats.currentDayNameAr} ${stats.currentDateAr}"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.PRAYER_TIMES -> {
                        val prayers = com.example.device.IslamicTimeCalculator.calculatePrayers(
                            lat = config.cityLatitude,
                            lng = config.cityLongitude,
                            timeZoneOffsetHours = config.cityTimeZone,
                            hijriOffsetDays = config.hijriDayOffset
                        )
                        views.setTextViewText(R.id.widget_title, "${config.cityName} • مواقيت الصلاة")
                        views.setTextViewText(R.id.widget_badge, "${prayers.nextPrayerCountdown} حتى ${prayers.nextPrayerNameAr}")
                        views.setTextViewText(R.id.widget_main_value, "${prayers.nextPrayerNameAr} @ ${prayers.nextPrayerTime}")
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "فجر: ${prayers.fajr} | ظهر: ${prayers.dhuhr} | عصر: ${prayers.asr} | مغرب: ${prayers.maghrib} | عشاء: ${prayers.isha}"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.ISLAMIC_CALENDAR_CARD -> {
                        val prayers = com.example.device.IslamicTimeCalculator.calculatePrayers(
                            lat = config.cityLatitude,
                            lng = config.cityLongitude,
                            timeZoneOffsetHours = config.cityTimeZone,
                            hijriOffsetDays = config.hijriDayOffset
                        )
                        views.setTextViewText(R.id.widget_title, "${stats.currentDayNameAr} • ${config.cityName}")
                        views.setTextViewText(R.id.widget_badge, prayers.hijriFormatted)
                        views.setTextViewText(R.id.widget_main_value, "${stats.currentDayNumber} ${stats.currentMonthAr}")
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "الصلاة القادمة: ${prayers.nextPrayerNameAr} (${prayers.nextPrayerCountdown})\nالمغرب: ${prayers.maghrib} | العشاء: ${prayers.isha}"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.WEATHER_TEMP -> {
                        views.setTextViewText(R.id.widget_title, "الطقس والحرارة • ${config.cityName}")
                        views.setTextViewText(R.id.widget_badge, "بدون نت")
                        views.setTextViewText(R.id.widget_main_value, "${config.temperatureCelsius}°م")
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "${config.weatherCondition} • رطوبة 45% • رياح 14 كم/س"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.QURAN_AUDIO -> {
                        views.setTextViewText(R.id.widget_title, "القرآن الكريم بصوت")
                        views.setTextViewText(R.id.widget_badge, "صوت مباشر 🔊")
                        views.setTextViewText(R.id.widget_main_value, config.quranSurahName)
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "القارئ: ${config.quranReciterName} • اضغط لتشغيل الصوت"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.APP_SHORTCUTS -> {
                        views.setTextViewText(R.id.widget_title, config.name)
                        views.setTextViewText(R.id.widget_badge, "إختصارات سريعة ⚡")
                        views.setTextViewText(R.id.widget_main_value, "واتساب • يوتيوب • تليجرام")
                        views.setTextViewText(
                            R.id.widget_sub_value,
                            "إطلاق فوري وتطبيقات مزدوجة مخصصة"
                        )
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.CALENDAR_MINI -> {
                        views.setTextViewText(R.id.widget_main_value, stats.currentDayNameAr)
                        views.setTextViewText(R.id.widget_sub_value, "${stats.currentDateAr}\nالساعة: ${stats.currentTimeFormatted}")
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                    WidgetType.MINIMAL_STAT -> {
                        views.setTextViewText(R.id.widget_main_value, config.noteContent)
                        views.setTextViewText(R.id.widget_sub_value, config.tagline.ifEmpty { "WidgetCraft Minimal" })
                        views.setViewVisibility(R.id.widget_action_row, View.GONE)
                    }
                }

                // Render dynamic background bitmap for custom gradient & corner radius
                try {
                    val bgBitmap = createWidgetBackgroundBitmap(
                        primaryColor = config.primaryColor().toInt(),
                        secondaryColor = config.secondaryColor().toInt(),
                        radiusDp = config.cornerRadiusDp
                    )
                    views.setImageViewBitmap(R.id.widget_bg_image, bgBitmap)
                } catch (e: Exception) {
                    // fallback to default shape
                }
            }

            // Click entire widget to launch WidgetCraft App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (config != null) {
                    putExtra("EXTRA_WIDGET_ID", config.id)
                }
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)

            return views
        }

        private fun createWidgetBackgroundBitmap(primaryColor: Int, secondaryColor: Int, radiusDp: Int): Bitmap {
            val width = 400
            val height = 240
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    primaryColor, secondaryColor,
                    Shader.TileMode.CLAMP
                )
            }
            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radiusPx = radiusDp * 2.5f
            canvas.drawRoundRect(rect, radiusPx, radiusPx, paint)

            // subtle inner border
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = 0x22FFFFFF
            }
            canvas.drawRoundRect(rect, radiusPx, radiusPx, borderPaint)

            return bitmap
        }
    }
}
