package com.example.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alerts.WidgetAlertReceiver
import com.example.data.local.StudioPreferencesRepository
import com.example.data.local.WidgetRepository
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.KeyboardTheme
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.data.model.WidgetAlert
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetType
import com.example.widget.CustomAppWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

import com.example.notifications.NotificationShortcutsHelper

sealed class AppScreen {
    object Home : AppScreen()
    data class Editor(val widgetId: String? = null, val baseTemplate: WidgetConfig? = null) : AppScreen()
    object Import : AppScreen()
    object AllWidgets : AppScreen()
    object WallpaperStudio : AppScreen()
    object ShortcutsStudio : AppScreen()
    object KeyboardStudio : AppScreen()
    object HiddenAppsVault : AppScreen()
    object AlertsStudio : AppScreen()
    object AiImageWidgetStudio : AppScreen()
    object MediaDownloader : AppScreen()
    data class VideoPlayer(val videoUri: String? = null, val videoTitle: String? = null) : AppScreen()
    data class InAppBrowser(val url: String = "") : AppScreen()
    object Settings : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WidgetRepository(application)
    private val studioPrefs = StudioPreferencesRepository(application)

    private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.Home))
    val currentScreen: StateFlow<AppScreen> = _screenStack.combine(MutableStateFlow(Unit)) { stack, _ ->
        stack.lastOrNull() ?: AppScreen.Home
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppScreen.Home)

    val allWidgets: StateFlow<List<WidgetConfig>> = repository.allWidgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLauncherWidget: StateFlow<WidgetConfig?> = repository.activeLauncherWidget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wallpaper = studioPrefs.wallpaper
    val shortcuts = studioPrefs.shortcuts
    val keyboardThemes = studioPrefs.keyboardThemes
    val activeKeyboardTheme = studioPrefs.activeKeyboardTheme
    val alerts = studioPrefs.alerts
    val vaultPin = studioPrefs.vaultPin
    val notificationShortcutsEnabled = studioPrefs.notificationShortcutsEnabled
    val pinnedNotificationShortcuts = studioPrefs.pinnedNotificationShortcuts
    val is24HourFormat = studioPrefs.is24HourFormat
    val useArabicNumerals = studioPrefs.useArabicNumerals

    fun toggle24HourFormat(enabled: Boolean) {
        studioPrefs.set24HourFormat(enabled)
        showToast(if (enabled) "تم التبديل إلى نظام 24 ساعة (00:00 - 23:59) ⏰" else "تم التبديل إلى نظام 12 ساعة (ص / م) ⏰")
    }

    fun toggleArabicNumerals() {
        val current = studioPrefs.useArabicNumerals.value
        val next = !current
        studioPrefs.setUseArabicNumerals(next)
        showToast(if (next) "تم تفعيل الأرقام العربية (١٢٣٤) في كامل التطبيق 🔢" else "تم تفعيل الأرقام الإنجليزية (1234) في كامل التطبيق 🔢")
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<WidgetType?>(null)
    val selectedFilterType = _selectedFilterType.asStateFlow()

    private val _uiToast = MutableStateFlow<String?>(null)
    val uiToast = _uiToast.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            if (studioPrefs.notificationShortcutsEnabled.value) {
                NotificationShortcutsHelper.showOrUpdateNotificationShortcuts(
                    application,
                    studioPrefs.shortcuts.value,
                    studioPrefs.pinnedNotificationShortcuts.value
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        return if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            true
        } else {
            false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: WidgetType?) {
        _selectedFilterType.value = type
    }

    fun showToast(message: String) {
        _uiToast.value = message
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun clearToast() {
        _uiToast.value = null
    }

    // ---------------- WIDGETS ----------------
    fun saveWidget(config: WidgetConfig) {
        viewModelScope.launch {
            repository.saveWidget(config)
            showToast("تم حفظ الويدجت بنجاح ✨")
            navigateBack()
        }
    }

    fun setActiveForLauncher(widgetId: String) {
        viewModelScope.launch {
            repository.setActiveForLauncher(widgetId)
            showToast("تم تعيين الويدجت كنشط للشاشة الرئيسية 📌")
        }
    }

    fun pinToHomeScreen(config: WidgetConfig) {
        val pinned = CustomAppWidgetProvider.pinWidgetToHomeScreen(getApplication(), config)
        if (pinned) {
            showToast("تم إرسال طلب التثبيت للشاشة الرئيسية بدون ملفات! تأكيد الإضافة...")
        } else {
            setActiveForLauncher(config.id)
            showToast("يمكنك إضافة الويدجت بالضغط مطولاً على الشاشة واختيار WidgetCraft")
        }
    }

    fun incrementCounter(widgetId: String) {
        viewModelScope.launch {
            repository.incrementCounter(widgetId)
        }
    }

    fun deleteWidget(widgetId: String) {
        viewModelScope.launch {
            repository.deleteWidget(widgetId)
            showToast("تم حذف الويدجت")
        }
    }

    fun duplicateWidget(widgetId: String) {
        viewModelScope.launch {
            val copy = repository.duplicateWidget(widgetId)
            if (copy != null) {
                showToast("تم إنشاء نسخة جديدة من الويدجت")
            }
        }
    }

    fun importWidgetFromJson(jsonString: String, onSuccess: (WidgetConfig) -> Unit) {
        viewModelScope.launch {
            val result = repository.importWidgetFromJson(jsonString)
            result.onSuccess { imported ->
                showToast("تم استيراد الويدجت بنجاح: ${imported.name} 🎉")
                onSuccess(imported)
            }.onFailure {
                showToast("فشل الاستيراد: تأكد من صحة كود JSON")
            }
        }
    }

    fun copyWidgetJsonToClipboard(config: WidgetConfig) {
        val json = config.toJsonString()
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("WidgetCraft JSON", json)
        clipboard.setPrimaryClip(clip)
        showToast("تم نسخ كود الويدجت للحافظة! 📋")
    }

    fun shareWidgetViaIntent(config: WidgetConfig) {
        val json = config.toJsonString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, json)
            putExtra(Intent.EXTRA_SUBJECT, "ويدجت WidgetCraft: ${config.name}")
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة كود الويدجت").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    fun installOnlineWidgetOffline(config: WidgetConfig, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val offlineConfig = config.copy(
                id = java.util.UUID.randomUUID().toString(),
                source = "ONLINE_DOWNLOAD",
                isActiveOnLauncher = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveWidget(offlineConfig)
            showToast("تم تنزيل \"${config.name}\" بنجاح! متاح للاستخدام فوراً ⚡")
            onComplete?.invoke()
        }
    }

    fun saveAiGeneratedWidget(config: WidgetConfig, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val offlineConfig = config.copy(
                id = java.util.UUID.randomUUID().toString(),
                source = "AI_GENERATED",
                isActiveOnLauncher = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveWidget(offlineConfig)
            showToast("تم حفظ الويدجت المولد بالذكاء الاصطناعي بنجاح! متاح للاستخدام فوراً 🪄")
            onComplete?.invoke()
        }
    }

    // ---------------- WALLPAPERS ----------------
    fun updateWallpaper(config: WallpaperConfig) {
        studioPrefs.setWallpaper(config)
        showToast("تم تحديث خلفية الشاشة والويدجات بنجاح 🎨")
    }

    fun setExternalWallpaperUri(uri: Uri) {
        val config = studioPrefs.wallpaper.value.copy(
            type = WallpaperType.EXTERNAL_IMAGE,
            customImageUri = uri.toString()
        )
        studioPrefs.setWallpaper(config)
        showToast("تم تطبيق الصورة الخارجية كخلفية بنجاح 🖼️")
    }

    // ---------------- SHORTCUTS & DUAL APPS ----------------
    fun addShortcut(shortcut: AppShortcut) {
        studioPrefs.addOrUpdateShortcut(shortcut)
        showToast("تمت إضافة الاختصار بنجاح")
    }

    fun updateShortcut(shortcut: AppShortcut) {
        studioPrefs.addOrUpdateShortcut(shortcut)
        showToast("تم تحديث شكل الاختصار")
    }

    fun deleteShortcut(id: String) {
        studioPrefs.deleteShortcut(id)
        if (notificationShortcutsEnabled.value) {
            val context = getApplication<Application>()
            NotificationShortcutsHelper.showOrUpdateNotificationShortcuts(
                context,
                shortcuts.value,
                pinnedNotificationShortcuts.value
            )
        }
        showToast("تم حذف الاختصار")
    }

    fun toggleShortcutActive(id: String) {
        studioPrefs.toggleShortcutActive(id)
        if (notificationShortcutsEnabled.value) {
            val context = getApplication<Application>()
            NotificationShortcutsHelper.showOrUpdateNotificationShortcuts(
                context,
                shortcuts.value,
                pinnedNotificationShortcuts.value
            )
        }
        showToast("تم تحديث حالة تفعيل الاختصار ⚡")
    }

    fun setShortcutLayer(id: String, newLayer: Int) {
        studioPrefs.setShortcutLayer(id, newLayer)
        showToast("تم تغيير رقم الطبقة إلى $newLayer")
    }

    fun toggleHideShortcut(id: String) {
        studioPrefs.toggleHideShortcut(id)
    }

    fun getInstalledApps(): List<AppShortcut> {
        val context = getApplication<Application>()
        val pm = context.packageManager
        return try {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            resolveInfos.mapNotNull { resolveInfo ->
                val name = resolveInfo.loadLabel(pm).toString()
                val pkg = resolveInfo.activityInfo.packageName
                if (pkg == context.packageName) null
                else {
                    AppShortcut(
                        id = "installed_${pkg}",
                        name = name,
                        customLabel = name,
                        packageName = pkg,
                        iconSymbol = "App",
                        iconColor = 0xFF38BDF8
                    )
                }
            }.distinctBy { it.packageName }.sortedBy { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun launchAppShortcut(shortcut: AppShortcut) {
        val context = getApplication<Application>()
        if (shortcut.externalUrl.isNotBlank()) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(shortcut.externalUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                showToast("فتح الرابط الخارجي: ${shortcut.displayLabel} 🌐")
                return
            } catch (e: Exception) {
                showToast("تعذر فتح الرابط الخارجي: تأكد من كتابة الرابط بشكل صحيح")
                return
            }
        }
        if (shortcut.packageName.isNotBlank()) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(shortcut.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                showToast("تشغيل: ${shortcut.displayLabel} 🚀")
                return
            } else {
                showToast("التطبيق (${shortcut.packageName}) غير مثبت على الهاتف")
                return
            }
        }
        showToast("فتح التطبيق: ${shortcut.displayLabel} (تطبيق محلي)")
    }

    fun pinShortcutToHomeScreen(shortcut: AppShortcut) {
        val context = getApplication<Application>()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val shortcutManager = context.getSystemService(android.content.pm.ShortcutManager::class.java)
            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
                val launchIntent = if (shortcut.externalUrl.isNotBlank()) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(shortcut.externalUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                } else if (shortcut.packageName.isNotBlank()) {
                    context.packageManager.getLaunchIntentForPackage(shortcut.packageName)
                        ?: Intent(context, com.example.MainActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                            putExtra("EXTRA_SHORTCUT_ID", shortcut.id)
                        }
                } else {
                    Intent(context, com.example.MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("EXTRA_SHORTCUT_ID", shortcut.id)
                    }
                }
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                val pinShortcutInfo = android.content.pm.ShortcutInfo.Builder(context, "shortcut_${shortcut.id}")
                    .setShortLabel(shortcut.displayLabel)
                    .setLongLabel(shortcut.name)
                    .setIcon(android.graphics.drawable.Icon.createWithResource(context, com.example.R.mipmap.ic_launcher))
                    .setIntent(launchIntent)
                    .build()
                shortcutManager.requestPinShortcut(pinShortcutInfo, null)
                showToast("تم إرسال طلب إضافة اختصار \"${shortcut.displayLabel}\" للشاشة الرئيسية! 📌")
                return
            }
        }
        showToast("تم تثبيت الاختصار \"${shortcut.displayLabel}\" محلياً")
    }

    // ---------------- KEYBOARD THEMES ----------------
    fun selectKeyboardTheme(theme: KeyboardTheme) {
        studioPrefs.setActiveKeyboardTheme(theme)
        showToast("تم تطبيق سمة لوحة المفاتيح: ${theme.name}")
    }

    fun applyAiGeneratedKeyboardTheme(theme: KeyboardTheme) {
        studioPrefs.addCustomKeyboardTheme(theme)
        studioPrefs.setActiveKeyboardTheme(theme)
        showToast("تم تشغيل لوحة المفاتيح المولدة بالذكاء الاصطناعي بنجاح! 🪄⌨️")
    }

    fun applyAiGeneratedWallpaper(config: WallpaperConfig) {
        studioPrefs.setWallpaper(config)
        showToast("تم تطبيق الخلفية المولدة بالذكاء الاصطناعي بنجاح! 🪄🎨")
    }

    fun importKeyboardThemeFromJson(jsonString: String) {
        val res = KeyboardTheme.fromJsonString(jsonString)
        res.onSuccess { theme ->
            studioPrefs.addCustomKeyboardTheme(theme)
            showToast("تم استيراد سمة لوحة المفاتيح بنجاح: ${theme.name} ⌨️")
        }.onFailure {
            showToast("فشل استيراد سمة لوحة المفاتيح: تأكد من صحة كود JSON")
        }
    }

    // ---------------- NOTIFICATION SHORTCUTS ----------------
    fun toggleNotificationShortcuts(enabled: Boolean) {
        studioPrefs.setNotificationShortcutsEnabled(enabled)
        val context = getApplication<Application>()
        if (enabled) {
            NotificationShortcutsHelper.showOrUpdateNotificationShortcuts(
                context,
                shortcuts.value,
                pinnedNotificationShortcuts.value
            )
            showToast("تم تفعيل إختصارات التطبيقات في مركز الإشعارات ⚡")
        } else {
            NotificationShortcutsHelper.cancelNotificationShortcuts(context)
            showToast("تم إيقاف إختصارات مركز الإشعارات")
        }
    }

    fun togglePinnedNotificationShortcut(shortcutId: String) {
        studioPrefs.togglePinnedNotificationShortcut(shortcutId)
        if (notificationShortcutsEnabled.value) {
            val context = getApplication<Application>()
            NotificationShortcutsHelper.showOrUpdateNotificationShortcuts(
                context,
                shortcuts.value,
                pinnedNotificationShortcuts.value
            )
        }
        showToast("تم تحديث إختصارات شريط الإشعارات 📌")
    }

    // ---------------- ALERTS & REMINDERS ----------------
    fun addOrUpdateAlert(alert: WidgetAlert) {
        studioPrefs.addOrUpdateAlert(alert)
        scheduleAlertSystem(alert)
        showToast("تم حفظ وتفعيل التنبيه ⏰: ${alert.title}")
    }

    fun deleteAlert(id: String) {
        studioPrefs.deleteAlert(id)
        showToast("تم حذف التنبيه")
    }

    fun toggleAlert(id: String) {
        studioPrefs.toggleAlertEnabled(id)
    }

    fun testTriggerAlert(alert: WidgetAlert) {
        val context = getApplication<Application>()
        val intent = Intent(context, WidgetAlertReceiver::class.java).apply {
            putExtra("EXTRA_ALERT_TITLE", alert.title)
            putExtra("EXTRA_ALERT_ID", alert.id)
            putExtra("EXTRA_VIBRATE", alert.vibrateEnabled)
            putExtra("EXTRA_SOUND_ENABLED", alert.soundEnabled)
            putExtra("EXTRA_SOUND_ID", alert.soundId)
            putExtra("EXTRA_CUSTOM_AUDIO_URI", alert.customAudioUri)
        }
        context.sendBroadcast(intent)
        showToast("تم إطلاق التنبيه التجريبي الآن: ${alert.soundTitle}")
    }

    private fun scheduleAlertSystem(alert: WidgetAlert) {
        if (!alert.isEnabled) return
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, WidgetAlertReceiver::class.java).apply {
            putExtra("EXTRA_ALERT_TITLE", alert.title)
            putExtra("EXTRA_ALERT_ID", alert.id)
            putExtra("EXTRA_VIBRATE", alert.vibrateEnabled)
            putExtra("EXTRA_SOUND_ENABLED", alert.soundEnabled)
            putExtra("EXTRA_SOUND_ID", alert.soundId)
            putExtra("EXTRA_CUSTOM_AUDIO_URI", alert.customAudioUri)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alert.hour)
            set(Calendar.MINUTE, alert.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            // fallback
        }
    }

    // ---------------- VAULT ----------------
    fun setVaultPin(pin: String) {
        studioPrefs.setVaultPin(pin)
        showToast("تم تعيين رمز الخزنة السري بنجاح 🔒")
    }
}
