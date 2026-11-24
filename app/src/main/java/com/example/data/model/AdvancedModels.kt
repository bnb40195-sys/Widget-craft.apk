package com.example.data.model

import org.json.JSONObject
import java.util.UUID

enum class IconShape(val labelAr: String) {
    ROUND("دائري أنيق"),
    SQUIRCLE("مربع منحني (سكويركل)"),
    ROUNDED_SQUARE("مربع حواف ناعمة"),
    HEXAGON("سداسي تكنو"),
    NEON_GLOW("نيون متوهج بدون إطار")
}

data class AppShortcut(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val customLabel: String = "",
    val packageName: String = "",
    val externalUrl: String = "",
    val iconShape: IconShape = IconShape.ROUND,
    val iconColor: Long = 0xFF2D6FE4,
    val iconSymbol: String = "App",
    val iconStyle: String = "BARE_LARGE",
    val isDualApp: Boolean = false,
    val dualLabel: String = "نسخة 2",
    val isHidden: Boolean = false,
    val isActive: Boolean = true,
    val layerIndex: Int = 1,
    val sortOrder: Int = 0
) {
    val displayLabel: String get() = customLabel.ifBlank { name }
    val isExternalSource: Boolean get() = externalUrl.isNotBlank() || (packageName.isNotBlank() && !packageName.startsWith("com.example"))
    fun getLayerLabel(useArabicNumerals: Boolean = false, isEnglish: Boolean = false): String {
        val numStr = com.example.util.NumeralFormatter.formatNumber(layerIndex, useArabicNumerals)
        return if (isEnglish) "(Layer $numStr)" else "(طبقة $numStr)"
    }
}

object PopularAppPresets {
    val presets = listOf(
        // External & Messaging Apps
        AppShortcut(name = "واتساب", customLabel = "WhatsApp", packageName = "com.whatsapp", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF25D366, iconSymbol = "Chat"),
        AppShortcut(name = "واتساب مزدوج", customLabel = "واتساب 2", packageName = "com.whatsapp", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF25D366, iconSymbol = "Chat", isDualApp = true, dualLabel = "مزدوج"),
        AppShortcut(name = "تليجرام", customLabel = "Telegram", packageName = "org.telegram.messenger", iconShape = IconShape.ROUND, iconColor = 0xFF2AABEE, iconSymbol = "Chat"),
        AppShortcut(name = "تليجرام للعمل", customLabel = "تليجرام 2", packageName = "org.telegram.messenger", iconShape = IconShape.ROUND, iconColor = 0xFF2AABEE, iconSymbol = "Chat", isDualApp = true, dualLabel = "عمل"),
        AppShortcut(name = "تيرمكس (Termux)", customLabel = "Termux", packageName = "com.termux", iconShape = IconShape.HEXAGON, iconColor = 0xFF10B981, iconSymbol = "Tools"),
        AppShortcut(name = "مشغل VLC خارجي", customLabel = "VLC Player", packageName = "org.videolan.vlc", iconShape = IconShape.ROUNDED_SQUARE, iconColor = 0xFFFF9800, iconSymbol = "Music"),
        AppShortcut(name = "متصفح بريف (Brave)", customLabel = "Brave", packageName = "com.brave.browser", iconShape = IconShape.ROUND, iconColor = 0xFFFF5722, iconSymbol = "Browser"),
        AppShortcut(name = "ديسكورد (Discord)", customLabel = "Discord", packageName = "com.discord", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF5865F2, iconSymbol = "Chat"),
        AppShortcut(name = "يوتيوب", customLabel = "YouTube", packageName = "com.google.android.youtube", iconShape = IconShape.SQUIRCLE, iconColor = 0xFFFF0000, iconSymbol = "PlayArrow"),
        AppShortcut(name = "سبوتيفاي", customLabel = "Spotify", packageName = "com.spotify.music", iconShape = IconShape.ROUND, iconColor = 0xFF1DB954, iconSymbol = "Music"),
        AppShortcut(name = "القرآن الكريم والأذكار", customLabel = "القرآن", packageName = "com.quran.labs.androidquran", iconShape = IconShape.SQUIRCLE, iconColor = 0xFF059669, iconSymbol = "Notes"),
        AppShortcut(name = "فيسبوك", customLabel = "Facebook", packageName = "com.facebook.katana", iconShape = IconShape.ROUND, iconColor = 0xFF1877F2, iconSymbol = "Browser"),
        AppShortcut(name = "إنستغرام", customLabel = "Instagram", packageName = "com.instagram.android", iconShape = IconShape.SQUIRCLE, iconColor = 0xFFE1306C, iconSymbol = "Camera"),
        AppShortcut(name = "إكس (تويتر)", customLabel = "X / Twitter", packageName = "com.twitter.android", iconShape = IconShape.ROUND, iconColor = 0xFF000000, iconSymbol = "Chat"),
        AppShortcut(name = "الهاتف", customLabel = "اتصال", packageName = "com.google.android.dialer", iconShape = IconShape.ROUND, iconColor = 0xFF10B981, iconSymbol = "Phone"),
        AppShortcut(name = "الكاميرا", customLabel = "الكاميرا", packageName = "com.google.android.GoogleCamera", iconShape = IconShape.HEXAGON, iconColor = 0xFFF59E0B, iconSymbol = "Camera"),
        AppShortcut(name = "المتصفح كروم", customLabel = "Chrome", packageName = "com.android.chrome", iconShape = IconShape.ROUND, iconColor = 0xFF4285F4, iconSymbol = "Browser"),
        AppShortcut(name = "الملاحظات", customLabel = "ملاحظاتي", packageName = "com.google.android.keep", iconShape = IconShape.SQUIRCLE, iconColor = 0xFFEAB308, iconSymbol = "Notes"),
        AppShortcut(name = "رابط ويب خارجي", customLabel = "موقع خارجي", externalUrl = "https://duckduckgo.com", iconShape = IconShape.ROUND, iconColor = 0xFF06B6D4, iconSymbol = "Browser"),
        AppShortcut(name = "الآلة الحاسبة", customLabel = "حاسبة", packageName = "com.google.android.calculator", iconShape = IconShape.ROUNDED_SQUARE, iconColor = 0xFF6366F1, iconSymbol = "Tools")
    )
}

enum class WallpaperType {
    PRESET_GRADIENT,
    EXTERNAL_IMAGE,
    SOLID_DARK,
    NEON_MESH
}

data class WallpaperConfig(
    val id: String = "default_wallpaper",
    val type: WallpaperType = WallpaperType.PRESET_GRADIENT,
    val presetIndex: Int = 0,
    val customImageUri: String? = null,
    val dimLevel: Float = 0.2f, // 0.0f to 0.8f
    val blurRadius: Float = 0f,
    val tintOverlayColor: Long = 0x00000000
)

data class KeyboardTheme(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val backgroundColor: Long = 0xFF0F172A,
    val keyColor: Long = 0xFF1E293B,
    val textColor: Long = 0xFFF8FAFC,
    val accentColor: Long = 0xFF38BDF8,
    val keyCornerRadiusDp: Int = 12,
    val isCustomImported: Boolean = false,
    val backgroundImageUri: String? = null,
    val keyAlpha: Float = 0.88f,
    val keyBorderColor: Long = 0x33FFFFFF
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("schema", "WidgetCraftKeyboard")
        json.put("id", id)
        json.put("name", name)
        json.put("backgroundColor", backgroundColor)
        json.put("keyColor", keyColor)
        json.put("textColor", textColor)
        json.put("accentColor", accentColor)
        json.put("keyCornerRadiusDp", keyCornerRadiusDp)
        json.put("backgroundImageUri", backgroundImageUri ?: "")
        json.put("keyAlpha", keyAlpha.toDouble())
        json.put("keyBorderColor", keyBorderColor)
        return json.toString(2)
    }

    companion object {
        fun fromJsonString(jsonStr: String): Result<KeyboardTheme> {
            return try {
                val json = JSONObject(jsonStr)
                val imgUri = json.optString("backgroundImageUri", "").ifEmpty { null }
                val theme = KeyboardTheme(
                    id = UUID.randomUUID().toString(),
                    name = json.optString("name", "لوحة مفاتيح مستوردة"),
                    backgroundColor = json.optLong("backgroundColor", 0xFF0F172A),
                    keyColor = json.optLong("keyColor", 0xFF1E293B),
                    textColor = json.optLong("textColor", 0xFFF8FAFC),
                    accentColor = json.optLong("accentColor", 0xFF38BDF8),
                    keyCornerRadiusDp = json.optInt("keyCornerRadiusDp", 12),
                    isCustomImported = true,
                    backgroundImageUri = imgUri,
                    keyAlpha = json.optDouble("keyAlpha", 0.88).toFloat(),
                    keyBorderColor = json.optLong("keyBorderColor", 0x33FFFFFF)
                )
                Result.success(theme)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val defaultThemes = listOf(
            KeyboardTheme(
                id = "kb_cyber_cyan",
                name = "سايبر نيون",
                backgroundColor = 0xFF0B1120,
                keyColor = 0xFF1E293B,
                textColor = 0xFFF8FAFC,
                accentColor = 0xFF38BDF8,
                keyCornerRadiusDp = 12
            ),
            KeyboardTheme(
                id = "kb_amoled_pure",
                name = "أسود AMOLED نقي",
                backgroundColor = 0xFF000000,
                keyColor = 0xFF121212,
                textColor = 0xFFFFFFFF,
                accentColor = 0xFF6366F1,
                keyCornerRadiusDp = 10
            ),
            KeyboardTheme(
                id = "kb_sunset_blaze",
                name = "شفق الغروب",
                backgroundColor = 0xFF2A0800,
                keyColor = 0xFF431407,
                textColor = 0xFFFED7AA,
                accentColor = 0xFFF97316,
                keyCornerRadiusDp = 14
            ),
            KeyboardTheme(
                id = "kb_emerald_matrix",
                name = "ماتريكس الزمرد",
                backgroundColor = 0xFF022C22,
                keyColor = 0xFF064E3B,
                textColor = 0xFFA7F3D0,
                accentColor = 0xFF10B981,
                keyCornerRadiusDp = 8
            )
        )
    }
}

data class WidgetAlert(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val hour: Int,
    val minute: Int,
    val repeatMode: String = "يومياً", // "يومياً", "مرة واحدة", "أيام العمل"
    val soundEnabled: Boolean = true,
    val soundId: String = "alert_sound", // "alert_sound", "adhan_chime", "quran_recitation", "custom_phone_audio", "silent"
    val soundTitle: String = "رنين التنبيه 🔔",
    val customAudioUri: String? = null,
    val vibrateEnabled: Boolean = true,
    val linkedWidgetId: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formatTime(is24Hour: Boolean = false): String {
        return if (is24Hour) {
            String.format(java.util.Locale.US, "%02d:%02d", hour, minute)
        } else {
            val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val period = if (hour < 12) "ص" else "م"
            String.format(java.util.Locale.US, "%02d:%02d %s", h, minute, period)
        }
    }

    val formattedTime: String
        get() = formatTime(false)
}

