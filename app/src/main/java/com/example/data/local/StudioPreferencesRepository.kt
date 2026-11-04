package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.KeyboardTheme
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.data.model.WidgetAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class StudioPreferencesRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("widgetcraft_studio_prefs", Context.MODE_PRIVATE)

    private val _wallpaper = MutableStateFlow(loadWallpaper())
    val wallpaper: StateFlow<WallpaperConfig> = _wallpaper.asStateFlow()

    private val _shortcuts = MutableStateFlow(loadShortcuts())
    val shortcuts: StateFlow<List<AppShortcut>> = _shortcuts.asStateFlow()

    private val _keyboardThemes = MutableStateFlow(loadKeyboardThemes())
    val keyboardThemes: StateFlow<List<KeyboardTheme>> = _keyboardThemes.asStateFlow()

    private val _activeKeyboardTheme = MutableStateFlow(loadActiveKeyboardTheme())
    val activeKeyboardTheme: StateFlow<KeyboardTheme> = _activeKeyboardTheme.asStateFlow()

    private val _alerts = MutableStateFlow(loadAlerts())
    val alerts: StateFlow<List<WidgetAlert>> = _alerts.asStateFlow()

    private val _vaultPin = MutableStateFlow(prefs.getString("vault_pin", "1234") ?: "1234")
    val vaultPin: StateFlow<String> = _vaultPin.asStateFlow()

    private val _notificationShortcutsEnabled = MutableStateFlow(prefs.getBoolean("notif_shortcuts_enabled", false))
    val notificationShortcutsEnabled: StateFlow<Boolean> = _notificationShortcutsEnabled.asStateFlow()

    private val _pinnedNotificationShortcuts = MutableStateFlow(loadPinnedNotificationShortcuts())
    val pinnedNotificationShortcuts: StateFlow<Set<String>> = _pinnedNotificationShortcuts.asStateFlow()

    // ---------------- WALLPAPERS ----------------
    fun setWallpaper(config: WallpaperConfig) {
        prefs.edit()
            .putString("wp_type", config.type.name)
            .putInt("wp_preset", config.presetIndex)
            .putString("wp_uri", config.customImageUri ?: "")
            .putFloat("wp_dim", config.dimLevel)
            .putFloat("wp_blur", config.blurRadius)
            .putLong("wp_tint", config.tintOverlayColor)
            .apply()
        _wallpaper.value = config
    }

    private fun loadWallpaper(): WallpaperConfig {
        val typeStr = prefs.getString("wp_type", WallpaperType.PRESET_GRADIENT.name)
        val type = try { WallpaperType.valueOf(typeStr!!) } catch (e: Exception) { WallpaperType.PRESET_GRADIENT }
        val preset = prefs.getInt("wp_preset", 0)
        val uri = prefs.getString("wp_uri", "")?.ifEmpty { null }
        val dim = prefs.getFloat("wp_dim", 0.2f)
        val blur = prefs.getFloat("wp_blur", 0f)
        val tint = prefs.getLong("wp_tint", 0L)
        return WallpaperConfig(
            type = type,
            presetIndex = preset,
            customImageUri = uri,
            dimLevel = dim,
            blurRadius = blur,
            tintOverlayColor = tint
        )
    }

    // ---------------- SHORTCUTS & DUAL APPS ----------------
    fun saveShortcuts(list: List<AppShortcut>) {
        val array = JSONArray()
        list.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("name", s.name)
            obj.put("customLabel", s.customLabel)
            obj.put("packageName", s.packageName)
            obj.put("iconShape", s.iconShape.name)
            obj.put("iconColor", s.iconColor)
            obj.put("iconSymbol", s.iconSymbol)
            obj.put("isDualApp", s.isDualApp)
            obj.put("dualLabel", s.dualLabel)
            obj.put("isHidden", s.isHidden)
            obj.put("sortOrder", s.sortOrder)
            array.put(obj)
        }
        prefs.edit().putString("shortcuts_json", array.toString()).apply()
        _shortcuts.value = list
    }

    fun addOrUpdateShortcut(shortcut: AppShortcut) {
        val current = _shortcuts.value.toMutableList()
        val index = current.indexOfFirst { it.id == shortcut.id }
        if (index >= 0) {
            current[index] = shortcut
        } else {
            current.add(shortcut)
        }
        saveShortcuts(current)
    }

    fun deleteShortcut(id: String) {
        val current = _shortcuts.value.filterNot { it.id == id }
        saveShortcuts(current)
    }

    fun toggleHideShortcut(id: String) {
        val current = _shortcuts.value.map {
            if (it.id == id) it.copy(isHidden = !it.isHidden) else it
        }
        saveShortcuts(current)
    }

    private fun loadShortcuts(): List<AppShortcut> {
        val jsonStr = prefs.getString("shortcuts_json", null)
        if (jsonStr.isNullOrBlank()) {
            val defaults = listOf(
                AppShortcut(id = "s_phone", name = "الهاتف", customLabel = "الهاتف", packageName = "com.google.android.dialer", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF10B981, iconSymbol = "Phone"),
                AppShortcut(id = "s_messages", name = "الرسائل", customLabel = "الرسائل", packageName = "com.google.android.apps.messaging", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF38BDF8, iconSymbol = "Chat"),
                AppShortcut(id = "s_camera", name = "الكاميرا", customLabel = "الكاميرا", packageName = "com.google.android.GoogleCamera", iconShape = IconShape.SQUIRCLE, iconColor = 0xFFF59E0B, iconSymbol = "Camera"),
                AppShortcut(id = "s_browser", name = "المتصفح", customLabel = "المتصفح", packageName = "com.android.chrome", iconShape = IconShape.SQUIRCLE, iconColor = 0xFFA855F7, iconSymbol = "Browser"),
                // Dual App Example
                AppShortcut(id = "s_whatsapp_dual", name = "واتساب 2", customLabel = "واتساب للعمل", packageName = "com.whatsapp", iconShape = IconShape.HEXAGON, iconColor = 0xFF059669, iconSymbol = "Dual", isDualApp = true, dualLabel = "مزدوج")
            )
            return defaults
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<AppShortcut>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AppShortcut(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        customLabel = obj.optString("customLabel", ""),
                        packageName = obj.optString("packageName", ""),
                        iconShape = try { IconShape.valueOf(obj.getString("iconShape")) } catch (e: Exception) { IconShape.SQUIRCLE },
                        iconColor = obj.optLong("iconColor", 0xFF38BDF8),
                        iconSymbol = obj.optString("iconSymbol", "App"),
                        isDualApp = obj.optBoolean("isDualApp", false),
                        dualLabel = obj.optString("dualLabel", "مزدوج"),
                        isHidden = obj.optBoolean("isHidden", false),
                        sortOrder = obj.optInt("sortOrder", i)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------------- KEYBOARD THEMES ----------------
    fun setActiveKeyboardTheme(theme: KeyboardTheme) {
        prefs.edit().putString("active_kb_theme_id", theme.id).apply()
        _activeKeyboardTheme.value = theme
    }

    fun addCustomKeyboardTheme(theme: KeyboardTheme) {
        val current = _keyboardThemes.value.toMutableList()
        current.add(theme)
        val array = JSONArray()
        current.filter { it.isCustomImported }.forEach { t ->
            array.put(JSONObject(t.toJsonString()))
        }
        prefs.edit().putString("custom_kb_themes", array.toString()).apply()
        _keyboardThemes.value = current
        setActiveKeyboardTheme(theme)
    }

    private fun loadKeyboardThemes(): List<KeyboardTheme> {
        val defaults = KeyboardTheme.defaultThemes.toMutableList()
        val customJson = prefs.getString("custom_kb_themes", null)
        if (!customJson.isNullOrBlank()) {
            try {
                val array = JSONArray(customJson)
                for (i in 0 until array.length()) {
                    val parsed = KeyboardTheme.fromJsonString(array.getJSONObject(i).toString())
                    parsed.getOrNull()?.let { defaults.add(it) }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
        return defaults
    }

    private fun loadActiveKeyboardTheme(): KeyboardTheme {
        val id = prefs.getString("active_kb_theme_id", "kb_cyber_cyan")
        val themes = loadKeyboardThemes()
        return themes.find { it.id == id } ?: themes.first()
    }

    // ---------------- ALERTS ----------------
    fun saveAlerts(list: List<WidgetAlert>) {
        val array = JSONArray()
        list.forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("title", a.title)
            obj.put("hour", a.hour)
            obj.put("minute", a.minute)
            obj.put("repeatMode", a.repeatMode)
            obj.put("soundEnabled", a.soundEnabled)
            obj.put("vibrateEnabled", a.vibrateEnabled)
            obj.put("linkedWidgetId", a.linkedWidgetId ?: "")
            obj.put("isEnabled", a.isEnabled)
            obj.put("createdAt", a.createdAt)
            array.put(obj)
        }
        prefs.edit().putString("widget_alerts_json", array.toString()).apply()
        _alerts.value = list
    }

    fun addOrUpdateAlert(alert: WidgetAlert) {
        val current = _alerts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == alert.id }
        if (idx >= 0) {
            current[idx] = alert
        } else {
            current.add(alert)
        }
        saveAlerts(current)
    }

    fun deleteAlert(id: String) {
        val current = _alerts.value.filterNot { it.id == id }
        saveAlerts(current)
    }

    fun toggleAlertEnabled(id: String) {
        val current = _alerts.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
        saveAlerts(current)
    }

    private fun loadAlerts(): List<WidgetAlert> {
        val jsonStr = prefs.getString("widget_alerts_json", null)
        if (jsonStr.isNullOrBlank()) {
            return listOf(
                WidgetAlert(title = "تذكير شرب الماء 💧", hour = 10, minute = 0, repeatMode = "يومياً"),
                WidgetAlert(title = "وقت التسبيح والاستغفار ✨", hour = 14, minute = 30, repeatMode = "يومياً"),
                WidgetAlert(title = "مراجعة المهام المسائية 📝", hour = 20, minute = 0, repeatMode = "يومياً")
            )
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<WidgetAlert>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WidgetAlert(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        hour = obj.getInt("hour"),
                        minute = obj.getInt("minute"),
                        repeatMode = obj.optString("repeatMode", "يومياً"),
                        soundEnabled = obj.optBoolean("soundEnabled", true),
                        vibrateEnabled = obj.optBoolean("vibrateEnabled", true),
                        linkedWidgetId = obj.optString("linkedWidgetId").ifEmpty { null },
                        isEnabled = obj.optBoolean("isEnabled", true),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------------- VAULT PIN ----------------
    fun setVaultPin(newPin: String) {
        prefs.edit().putString("vault_pin", newPin).apply()
        _vaultPin.value = newPin
    }

    // ---------------- NOTIFICATION SHORTCUTS ----------------
    fun setNotificationShortcutsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notif_shortcuts_enabled", enabled).apply()
        _notificationShortcutsEnabled.value = enabled
    }

    fun togglePinnedNotificationShortcut(id: String) {
        val current = _pinnedNotificationShortcuts.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        val array = JSONArray()
        current.forEach { array.put(it) }
        prefs.edit().putString("pinned_notif_shortcuts", array.toString()).apply()
        _pinnedNotificationShortcuts.value = current
    }

    private fun loadPinnedNotificationShortcuts(): Set<String> {
        val jsonStr = prefs.getString("pinned_notif_shortcuts", null)
        if (jsonStr.isNullOrBlank()) {
            return setOf("s_phone", "s_messages", "s_camera", "s_browser", "s_whatsapp_dual")
        }
        return try {
            val array = JSONArray(jsonStr)
            val set = mutableSetOf<String>()
            for (i in 0 until array.length()) {
                set.add(array.getString(i))
            }
            set
        } catch (e: Exception) {
            setOf("s_phone", "s_messages", "s_camera", "s_browser")
        }
    }
}
