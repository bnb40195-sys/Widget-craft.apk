package com.example.data.model

import org.json.JSONObject
import java.util.UUID

enum class WidgetType(val id: String, val titleAr: String, val titleEn: String, val iconName: String) {
    DIGITAL_CLOCK("digital_clock", "ساعة رقمية وتاريخ", "Digital Clock & Date", "Schedule"),
    ANALOG_CLOCK("analog_clock", "ساعة عقارب (رقاص)", "Analog Clock Face", "WatchLater"),
    PRAYER_TIMES("prayer_times", "مواقيت الصلاة وموعد الأذان", "Prayer Times Gadget", "Mosque"),
    ISLAMIC_CALENDAR_CARD("islamic_calendar_card", "التقويم الهجري ومواقيت الصلاة", "Hijri Calendar & Salah Card", "CalendarToday"),
    WEATHER_TEMP("weather_temp", "ويدجت الحرارة والطقس", "Weather & Temperature", "WbSunny"),
    QURAN_AUDIO("quran_audio", "ويدجت القرآن الكريم الصوتي", "Holy Quran Audio Widget", "VolumeUp"),
    DAILY_QURAN_AYAH("daily_quran_ayah", "آية قرآنية يومية (كل يوم آية)", "Daily Quran Ayah", "MenuBook"),
    TODAY_IN_HISTORY("today_in_history", "حدث اليوم (تاريخ وإسلاميات)", "Today in History", "History"),
    APP_SHORTCUTS("app_shortcuts", "إختصارات التطبيقات", "App Shortcuts Bar", "Apps"),
    BATTERY_DEVICE("battery_device", "مؤشر البطارية وحالة الجهاز", "Battery & Device Status", "BatteryChargingFull"),
    QUICK_NOTES("quick_notes", "مفكرة وملاحظة سريعة", "Quick Note & Memo", "EditNote"),
    DAILY_QUOTE("daily_quote", "حكمة وعبارة اليوم", "Daily Quote & Wisdom", "FormatQuote"),
    HABIT_COUNTER("habit_counter", "عداد الإنجاز والتسبيح", "Habit & Tally Counter", "AddCircle"),
    CALENDAR_MINI("calendar_mini", "تقويم شهري مصغر", "Mini Calendar", "CalendarMonth"),
    MINIMAL_STAT("minimal_stat", "إحصائية وعنوان مخصص", "Custom Stat Banner", "AutoAwesome")
}

enum class WidgetTheme(
    val id: String,
    val titleAr: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val textColor: Long,
    val accentColor: Long
) {
    CYBER_NEON("cyber_neon", "سايبر نيون", 0xFF0F172A, 0xFF1E1B4B, 0xFF38BDF8, 0xFFF43F5E),
    SUNSET_BLAZE("sunset_blaze", "غروب الشفق", 0xFF431407, 0xFF7C2D12, 0xFFFDBA74, 0xFFF97316),
    EMERALD_MINT("emerald_mint", "زمرد الغابة", 0xFF064E3B, 0xFF065F46, 0xFF6EE7B7, 0xFF10B981),
    OLED_MIDNIGHT("oled_midnight", "أسود داكن OLED", 0xFF050505, 0xFF171717, 0xFFFAFAFA, 0xFF6366F1),
    ROSE_GOLD("rose_gold", "وردي ذهبي أنيق", 0xFF4C0519, 0xFF881337, 0xFFFDA4AF, 0xFFFB7185),
    COSMIC_VIOLET("cosmic_violet", "بنفسجي كوني", 0xFF2E1065, 0xFF4C1D95, 0xFFC4B5FD, 0xFFA855F7),
    FROSTED_GLASS("frosted_glass", "زجاج معتم شبه شفاف", 0xFF1E293B, 0xFF334155, 0xFFE2E8F0, 0xFF0EA5E9),
    MINIMAL_WHITE("minimal_white", "أبيض ناصع مينيمال", 0xFFF8FAFC, 0xFFE2E8F0, 0xFF0F172A, 0xFF2563EB),
    ROYAL_AMBER("royal_amber", "عنبر ملكي", 0xFF451A03, 0xFF78350F, 0xFFFDE68A, 0xFFD97706)
}

enum class WidgetSize(val label: String, val cells: String, val widthSpan: Int, val heightSpan: Int) {
    SMALL_2x2("صغير (2x2)", "2x2", 2, 2),
    MEDIUM_4x2("متوسط (4x2)", "4x2", 4, 2),
    WIDE_4x1("شريطي عريض (4x1)", "4x1", 4, 1),
    LARGE_4x4("كبير كامل (4x4)", "4x4", 4, 4)
}

data class WidgetConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val tagline: String = "",
    val type: WidgetType = WidgetType.DIGITAL_CLOCK,
    val theme: WidgetTheme = WidgetTheme.CYBER_NEON,
    val customPrimaryColor: Long? = null,
    val customSecondaryColor: Long? = null,
    val customTextColor: Long? = null,
    val cornerRadiusDp: Int = 22,
    val size: WidgetSize = WidgetSize.MEDIUM_4x2,
    val showDate: Boolean = true,
    val showBattery: Boolean = true,
    val showSeconds: Boolean = false,
    val noteContent: String = "أكمل مهامك اليومية بثقة وتوكل على الله ✨",
    val counterValue: Int = 0,
    val counterStep: Int = 1,
    val counterLabel: String = "مرات التسبيح / الإنجاز",
    val quoteArabic: String = "البدايات الصعبة تصنع النهايات العظيمة.",
    val quoteEnglish: String = "Great things never came from comfort zones.",
    val quoteAuthor: String = "حكمة اليوم",
    // New fields for Prayer Times, Analog Clock, and Weather
    val cityName: String = "وهران (Oran)",
    val cityLatitude: Double = 35.6976,
    val cityLongitude: Double = -0.6337,
    val cityTimeZone: Double = 1.0,
    val temperatureCelsius: Int = 24,
    val weatherCondition: String = "مشمس ومعتدل",
    val analogStyle: String = "ALG_DARK", // "ALG_DARK", "MINIMAL_WHITE", "NEON_CYBER", "CLASSIC_GOLD"
    val hijriDayOffset: Int = 0,
    val quranSurahName: String = "سورة الفاتحة",
    val quranReciterName: String = "مشاري راشد العفاسي",
    val quranAyahText: String = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
    val quranAyahSurah: String = "سورة الشرح",
    val quranAyahNumber: Int = 6,
    val historyEventTitle: String = "فتح مكة المكرمة",
    val historyEventDate: String = "20 رمضان 8 هـ",
    val historyEventYear: String = "630 م",
    val historyEventDescription: String = "أعظم انتصارات المسلمين ودخول الناس في دين الله أفواجاً وتطهير الكعبة المشرفة.",
    val isActiveOnLauncher: Boolean = false,
    val isFavorite: Boolean = false,
    val source: String = "CREATED", // "CREATED", "IMPORTED", "PRESET"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun primaryColor(): Long = customPrimaryColor ?: theme.primaryColor
    fun secondaryColor(): Long = customSecondaryColor ?: theme.secondaryColor
    fun textColor(): Long = customTextColor ?: theme.textColor

    fun toJsonString(): String {
        val json = JSONObject()
        json.put("schemaVersion", 2)
        json.put("app", "WidgetCraft")
        json.put("id", id)
        json.put("name", name)
        json.put("tagline", tagline)
        json.put("type", type.id)
        json.put("theme", theme.id)
        if (customPrimaryColor != null) json.put("customPrimaryColor", customPrimaryColor)
        if (customSecondaryColor != null) json.put("customSecondaryColor", customSecondaryColor)
        if (customTextColor != null) json.put("customTextColor", customTextColor)
        json.put("cornerRadiusDp", cornerRadiusDp)
        json.put("size", size.name)
        json.put("showDate", showDate)
        json.put("showBattery", showBattery)
        json.put("showSeconds", showSeconds)
        json.put("noteContent", noteContent)
        json.put("counterValue", counterValue)
        json.put("counterStep", counterStep)
        json.put("counterLabel", counterLabel)
        json.put("quoteArabic", quoteArabic)
        json.put("quoteEnglish", quoteEnglish)
        json.put("quoteAuthor", quoteAuthor)
        json.put("cityName", cityName)
        json.put("cityLatitude", cityLatitude)
        json.put("cityLongitude", cityLongitude)
        json.put("cityTimeZone", cityTimeZone)
        json.put("temperatureCelsius", temperatureCelsius)
        json.put("weatherCondition", weatherCondition)
        json.put("analogStyle", analogStyle)
        json.put("hijriDayOffset", hijriDayOffset)
        json.put("quranSurahName", quranSurahName)
        json.put("quranReciterName", quranReciterName)
        json.put("quranAyahText", quranAyahText)
        json.put("quranAyahSurah", quranAyahSurah)
        json.put("quranAyahNumber", quranAyahNumber)
        json.put("historyEventTitle", historyEventTitle)
        json.put("historyEventDate", historyEventDate)
        json.put("historyEventYear", historyEventYear)
        json.put("historyEventDescription", historyEventDescription)
        json.put("source", "IMPORTED")
        json.put("exportedAt", System.currentTimeMillis())
        return json.toString(2)
    }

    companion object {
        fun fromJsonString(jsonStr: String): Result<WidgetConfig> {
            return try {
                val json = JSONObject(jsonStr)
                val typeId = json.optString("type", WidgetType.DIGITAL_CLOCK.id)
                val type = WidgetType.values().find { it.id == typeId || it.name == typeId } ?: WidgetType.DIGITAL_CLOCK

                val themeId = json.optString("theme", WidgetTheme.CYBER_NEON.id)
                val theme = WidgetTheme.values().find { it.id == themeId || it.name == themeId } ?: WidgetTheme.CYBER_NEON

                val sizeName = json.optString("size", WidgetSize.MEDIUM_4x2.name)
                val size = try {
                    WidgetSize.valueOf(sizeName)
                } catch (e: Exception) {
                    WidgetSize.MEDIUM_4x2
                }

                val config = WidgetConfig(
                    id = UUID.randomUUID().toString(),
                    name = json.optString("name", "ويدجت مستورد"),
                    tagline = json.optString("tagline", "تم استيراده من الخارج"),
                    type = type,
                    theme = theme,
                    customPrimaryColor = if (json.has("customPrimaryColor")) json.getLong("customPrimaryColor") else null,
                    customSecondaryColor = if (json.has("customSecondaryColor")) json.getLong("customSecondaryColor") else null,
                    customTextColor = if (json.has("customTextColor")) json.getLong("customTextColor") else null,
                    cornerRadiusDp = json.optInt("cornerRadiusDp", 22),
                    size = size,
                    showDate = json.optBoolean("showDate", true),
                    showBattery = json.optBoolean("showBattery", true),
                    showSeconds = json.optBoolean("showSeconds", false),
                    noteContent = json.optString("noteContent", "ملاحظة جديدة"),
                    counterValue = json.optInt("counterValue", 0),
                    counterStep = json.optInt("counterStep", 1),
                    counterLabel = json.optString("counterLabel", "العداد"),
                    quoteArabic = json.optString("quoteArabic", "العلم نور والعمل به نجاة."),
                    quoteEnglish = json.optString("quoteEnglish", "Knowledge is power."),
                    quoteAuthor = json.optString("quoteAuthor", "مأثورة"),
                    cityName = json.optString("cityName", "وهران (Oran)"),
                    cityLatitude = json.optDouble("cityLatitude", 35.6976),
                    cityLongitude = json.optDouble("cityLongitude", -0.6337),
                    cityTimeZone = json.optDouble("cityTimeZone", 1.0),
                    temperatureCelsius = json.optInt("temperatureCelsius", 24),
                    weatherCondition = json.optString("weatherCondition", "مشمس ومعتدل"),
                    analogStyle = json.optString("analogStyle", "ALG_DARK"),
                    hijriDayOffset = json.optInt("hijriDayOffset", 0),
                    quranSurahName = json.optString("quranSurahName", "سورة الفاتحة"),
                    quranReciterName = json.optString("quranReciterName", "مشاري راشد العفاسي"),
                    quranAyahText = json.optString("quranAyahText", "إِنَّ مَعَ الْعُسْرِ يُسْرًا"),
                    quranAyahSurah = json.optString("quranAyahSurah", "سورة الشرح"),
                    quranAyahNumber = json.optInt("quranAyahNumber", 6),
                    historyEventTitle = json.optString("historyEventTitle", "فتح مكة المكرمة"),
                    historyEventDate = json.optString("historyEventDate", "20 رمضان 8 هـ"),
                    historyEventYear = json.optString("historyEventYear", "630 م"),
                    historyEventDescription = json.optString("historyEventDescription", "أعظم انتصارات المسلمين ودخول الناس في دين الله أفواجاً وتطهير الكعبة المشرفة."),
                    isActiveOnLauncher = false,
                    isFavorite = false,
                    source = "IMPORTED",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                Result.success(config)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
