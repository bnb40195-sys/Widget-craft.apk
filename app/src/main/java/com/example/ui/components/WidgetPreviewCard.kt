package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackManager
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetType
import com.example.device.CityLocation
import com.example.device.DeviceStats
import com.example.device.DeviceStatsManager
import com.example.device.IslamicTimeCalculator
import com.example.device.PrayerSchedule
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WidgetPreviewCard(
    config: WidgetConfig,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    onCounterIncrement: ((String) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val is24Hour = remember(context) {
        val prefs = context.getSharedPreferences("studio_prefs", android.content.Context.MODE_PRIVATE)
        prefs.getBoolean("is_24_hour_format", false)
    }
    var liveStats by remember { mutableStateOf(DeviceStatsManager.getDeviceStats(context, is24Hour)) }
    var prayerSchedule by remember {
        mutableStateOf(
            IslamicTimeCalculator.calculatePrayerTimes(
                city = CityLocation(
                    name = config.cityName,
                    country = "",
                    latitude = config.cityLatitude,
                    longitude = config.cityLongitude,
                    timeZoneOffsetHours = config.cityTimeZone
                ),
                hijriOffsetDays = config.hijriDayOffset
            )
        )
    }

    // Live clock update every second
    LaunchedEffect(config.cityName, config.cityLatitude) {
        while (true) {
            delay(1000)
            val prefNow = context.getSharedPreferences("studio_prefs", android.content.Context.MODE_PRIVATE)
            val current24 = prefNow.getBoolean("is_24_hour_format", false)
            liveStats = DeviceStatsManager.getDeviceStats(context, current24)
            prayerSchedule = IslamicTimeCalculator.calculatePrayerTimes(
                city = CityLocation(
                    name = config.cityName,
                    country = "",
                    latitude = config.cityLatitude,
                    longitude = config.cityLongitude,
                    timeZoneOffsetHours = config.cityTimeZone
                ),
                hijriOffsetDays = config.hijriDayOffset
            )
        }
    }

    val primaryColor = Color(config.primaryColor())
    val secondaryColor = Color(config.secondaryColor())
    val textColor = Color(config.textColor())
    val cornerRadius = config.cornerRadiusDp.dp

    // Adaptive height based on widget type and size
    val cardHeight = when (config.type) {
        WidgetType.ANALOG_CLOCK -> 210.dp
        WidgetType.PRAYER_TIMES -> 220.dp
        WidgetType.ISLAMIC_CALENDAR_CARD -> 360.dp
        WidgetType.WEATHER_TEMP -> 125.dp
        WidgetType.QURAN_AUDIO -> 200.dp
        else -> when (config.size) {
            WidgetSize.SMALL_2x2 -> 160.dp
            WidgetSize.WIDE_4x1 -> 110.dp
            WidgetSize.MEDIUM_4x2 -> 180.dp
            WidgetSize.LARGE_4x4 -> 280.dp
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(cornerRadius), spotColor = primaryColor)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor)
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(14.dp)
            .testTag("widget_card_${config.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Title & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = config.name,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (config.isActiveOnLauncher) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "على الشاشة 📌",
                                color = Color(0xFF34D399),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "نشط",
                            color = textColor.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Main Body Content depending on widget type
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (config.type) {
                    WidgetType.ANALOG_CLOCK -> {
                        AnalogClockBody(config = config, textColor = textColor)
                    }
                    WidgetType.PRAYER_TIMES -> {
                        PrayerTimesBody(schedule = prayerSchedule, stats = liveStats, textColor = textColor)
                    }
                    WidgetType.ISLAMIC_CALENDAR_CARD -> {
                        IslamicCalendarCardBody(schedule = prayerSchedule, textColor = textColor)
                    }
                    WidgetType.WEATHER_TEMP -> {
                        WeatherTempBody(config = config, textColor = textColor)
                    }
                    WidgetType.QURAN_AUDIO -> {
                        QuranAudioBody(config = config, textColor = textColor, isInteractive = isInteractive)
                    }
                    WidgetType.APP_SHORTCUTS -> {
                        AppShortcutsWidgetBody(textColor = textColor)
                    }
                    WidgetType.DIGITAL_CLOCK -> {
                        DigitalClockBody(config = config, stats = liveStats, textColor = textColor)
                    }
                    WidgetType.BATTERY_DEVICE -> {
                        BatteryDeviceBody(config = config, stats = liveStats, textColor = textColor)
                    }
                    WidgetType.QUICK_NOTES -> {
                        QuickNotesBody(config = config, textColor = textColor)
                    }
                    WidgetType.DAILY_QUOTE -> {
                        DailyQuoteBody(config = config, textColor = textColor)
                    }
                    WidgetType.HABIT_COUNTER -> {
                        HabitCounterBody(
                            config = config,
                            textColor = textColor,
                            isInteractive = isInteractive,
                            onIncrement = {
                                onCounterIncrement?.invoke(config.id)
                            }
                        )
                    }
                    WidgetType.CALENDAR_MINI -> {
                        MiniCalendarBody(stats = liveStats, textColor = textColor)
                    }
                    WidgetType.MINIMAL_STAT -> {
                        MinimalStatBody(config = config, textColor = textColor)
                    }
                }
            }

            // Footer Subtitle / Status / Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val subText = when (config.type) {
                    WidgetType.ANALOG_CLOCK -> config.cityName
                    WidgetType.PRAYER_TIMES -> prayerSchedule.cityName
                    WidgetType.ISLAMIC_CALENDAR_CARD -> "${prayerSchedule.hijriYear} هـ • ${prayerSchedule.gregorianYear} م"
                    WidgetType.WEATHER_TEMP -> "${config.cityName} • ${config.weatherCondition}"
                    WidgetType.QURAN_AUDIO -> config.quranReciterName
                    WidgetType.DIGITAL_CLOCK -> {
                        val datePart = if (config.showDate) "${liveStats.currentDayNameAr}، ${liveStats.currentDateAr}" else ""
                        val batPart = if (config.showBattery) " • ⚡ ${liveStats.batteryPct}%" else ""
                        "$datePart$batPart".ifEmpty { "بدون إنترنت" }
                    }
                    WidgetType.BATTERY_DEVICE -> "ذاكرة RAM: ${liveStats.freeRamMb} MB متاحة"
                    WidgetType.QUICK_NOTES -> "تحديث مستمر محلياً"
                    WidgetType.DAILY_QUOTE -> "— ${config.quoteAuthor}"
                    WidgetType.HABIT_COUNTER -> config.counterLabel
                    WidgetType.CALENDAR_MINI -> "الساعة: ${liveStats.currentTimeFormatted}"
                    WidgetType.APP_SHORTCUTS -> "إختصارات سريعة وتطبيقات"
                    WidgetType.MINIMAL_STAT -> config.tagline.ifEmpty { "WidgetCraft Studio" }
                }

                Text(
                    text = subText,
                    color = textColor.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = config.size.cells,
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. ANALOG CLOCK (الساعة رقاص / عقارب مثل ما في الصورة)
// -------------------------------------------------------------
@Composable
private fun AnalogClockBody(config: WidgetConfig, textColor: Color) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            calendar = Calendar.getInstance()
        }
    }

    val hours = calendar.get(Calendar.HOUR)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color(0xFF171717))
            .border(2.dp, Color(0xFF333333), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 10f

            // Draw 12 Tick Marks
            for (i in 0 until 60) {
                val isHourTick = i % 5 == 0
                val angle = Math.toRadians((i * 6.0) - 90.0)
                val tickLen = if (isHourTick) 10f else 4f
                val startDist = radius - tickLen
                val start = Offset(
                    x = (center.x + startDist * cos(angle)).toFloat(),
                    y = (center.y + startDist * sin(angle)).toFloat()
                )
                val end = Offset(
                    x = (center.x + radius * cos(angle)).toFloat(),
                    y = (center.y + radius * sin(angle)).toFloat()
                )
                drawLine(
                    color = if (isHourTick) Color.White else Color.Gray.copy(alpha = 0.6f),
                    start = start,
                    end = end,
                    strokeWidth = if (isHourTick) 2.5f else 1.2f,
                    cap = StrokeCap.Round
                )
            }

            // Draw Hour Hand
            val hourAngle = Math.toRadians(((hours % 12 + minutes / 60f) * 30.0) - 90.0)
            val hourLength = radius * 0.52f
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(
                    x = (center.x + hourLength * cos(hourAngle)).toFloat(),
                    y = (center.y + hourLength * sin(hourAngle)).toFloat()
                ),
                strokeWidth = 4.5f,
                cap = StrokeCap.Round
            )

            // Draw Minute Hand
            val minuteAngle = Math.toRadians(((minutes + seconds / 60f) * 6.0) - 90.0)
            val minuteLength = radius * 0.76f
            drawLine(
                color = Color(0xFFF1F5F9),
                start = center,
                end = Offset(
                    x = (center.x + minuteLength * cos(minuteAngle)).toFloat(),
                    y = (center.y + minuteLength * sin(minuteAngle)).toFloat()
                ),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Draw Second Hand
            val secondAngle = Math.toRadians((seconds * 6.0) - 90.0)
            val secondLength = radius * 0.85f
            drawLine(
                color = Color(0xFFE2E8F0),
                start = center,
                end = Offset(
                    x = (center.x + secondLength * cos(secondAngle)).toFloat(),
                    y = (center.y + secondLength * sin(secondAngle)).toFloat()
                ),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )

            // Center Golden Pivot Pin (مثل ما في الصورة)
            drawCircle(
                color = Color(0xFFEAB308),
                radius = 5.5f,
                center = center
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 2.5f,
                center = center
            )
        }

        // Numerals and "ALG" Badge like user's image
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Text(
                text = "12",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ALG",
                color = Color.LightGray.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

// -------------------------------------------------------------
// 2. PRAYER TIMES GADGET (مواقيت الصلاة مثل ما في الصورة بالأعلى)
// -------------------------------------------------------------
@Composable
private fun PrayerTimesBody(schedule: PrayerSchedule, stats: DeviceStats, textColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Top Row: Dates on left, Big Time on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "${schedule.dayNameArabic}، ${schedule.dateHijriFormatted}",
                    color = textColor.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = schedule.dateGregorianFormatted,
                    color = textColor.copy(alpha = 0.65f),
                    fontSize = 11.sp
                )
            }

            Text(
                text = stats.currentTimeFormatted,
                color = textColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Middle Row: Next Prayer countdown (Gold text) + Location
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = schedule.timeRemainingFormatted,
                color = Color(0xFFFDE047), // Glowing Gold
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = schedule.cityName,
                    color = textColor.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Bottom Row: All 6 Prayer Times (الفجر، الشروق، الظهر، العصر، المغرب، العشاء)
        val prayerSlots = listOf(
            Triple("الفجر", schedule.fajr, false),
            Triple("الشروق", schedule.shurooq, false),
            Triple("الظهر", schedule.dhuhr, schedule.currentPrayerActive == "الظهر"),
            Triple("العصر", schedule.asr, schedule.currentPrayerActive == "العصر"),
            Triple("المغرب", schedule.maghrib, schedule.nextPrayerName == "المغرب"),
            Triple("العشاء", schedule.isha, schedule.currentPrayerActive == "العشاء")
        )

        Surface(
            color = Color.Black.copy(alpha = 0.25f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                prayerSlots.forEach { (name, time, isHighlighted) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = name,
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = time,
                            color = when {
                                name == "العصر" -> Color(0xFF4ADE80) // Green in screenshot
                                name == "المغرب" -> Color(0xFFFACC15) // Gold in screenshot
                                else -> textColor
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ISLAMIC CALENDAR CARD (بطاقة التقويم والمواقيت الكبرى مثل أسفل يمين الصورة)
// -------------------------------------------------------------
@Composable
private fun IslamicCalendarCardBody(schedule: PrayerSchedule, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Top Box: Islamic Day Calligraphy "الأحد" with side years
        Surface(
            color = Color.White.copy(alpha = 0.08f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${schedule.gregorianYear}",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
                Text(
                    text = schedule.dayNameArabic,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "${schedule.hijriYear}",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }

        // Middle Row: Months (جمادى الأولى / أكتوبر) and Day Numbers (13 / 25)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Gregorian Day
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = schedule.gregorianMonthNameArabic,
                        color = textColor.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${schedule.gregorianDay}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Hijri Day
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = schedule.hijriMonthNameArabic,
                        color = textColor.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${schedule.hijriDay}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Bottom 6-Cell Prayer Grid (الفجر، الشمس، الظهر | العصر، المغرب، العشاء)
        val gridRows = listOf(
            listOf(Triple("الظهر", schedule.dhuhr, false), Triple("الشمس", schedule.shurooq, false), Triple("الفجر", schedule.fajr, false)),
            listOf(Triple("العشاء", schedule.isha, false), Triple("المغرب", schedule.maghrib, true), Triple("العصر", schedule.asr, true))
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            gridRows.forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowSlots.forEach { (name, time, isHighlighted) ->
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isHighlighted) Color(0xFFFACC15).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = name,
                                    color = textColor.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = time,
                                    color = if (isHighlighted) Color(0xFFFACC15) else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. WEATHER & TEMPERATURE WIDGET (ويدجت الحرارة والطقس)
// -------------------------------------------------------------
@Composable
private fun WeatherTempBody(config: WidgetConfig, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "${config.temperatureCelsius}",
                        color = textColor,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "°C",
                        color = Color(0xFFFBBF24),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = config.weatherCondition,
                    color = textColor.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "رطوبة: 45%", color = textColor.copy(alpha = 0.7f), fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Air, contentDescription = null, tint = Color(0xFFA7F3D0), modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "رياح: 12 كم/س", color = textColor.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 5. HOLY QURAN AUDIO WIDGET (ويدجت القرآن الكريم بصوت)
// -------------------------------------------------------------
@Composable
private fun QuranAudioBody(config: WidgetConfig, textColor: Color, isInteractive: Boolean) {
    val context = LocalContext.current
    val isPlaying by AudioPlaybackManager.isPlaying.collectAsState()
    val trackTitle by AudioPlaybackManager.currentTrackTitle.collectAsState()
    val reciter by AudioPlaybackManager.currentReciter.collectAsState()
    val ayahText by AudioPlaybackManager.currentAyahText.collectAsState()
    val progress by AudioPlaybackManager.progress.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Surah & Reciter Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPlaying) trackTitle else config.quranSurahName,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isPlaying) reciter else "القارئ: ${config.quranReciterName}",
                    color = Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Audio Wave / Playing Status
            Surface(
                color = if (isPlaying) Color(0xFF10B981).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = if (isPlaying) Color(0xFF34D399) else textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "جاري التشغيل..." else "جاهز",
                        color = if (isPlaying) Color(0xFF34D399) else textColor.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Ayah Text Box
        Surface(
            color = Color.Black.copy(alpha = 0.2f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "« $ayahText »",
                color = textColor.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Audio Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF10B981),
            trackColor = Color.White.copy(alpha = 0.15f)
        )

        // Interactive Play / Pause / Stop Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                onClick = {
                    if (isInteractive) {
                        AudioPlaybackManager.togglePlayPause(context)
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color(0xFF090D16)
                ),
                modifier = Modifier
                    .size(46.dp)
                    .testTag("btn_quran_play_toggle")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isPlaying) {
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (isInteractive) {
                            AudioPlaybackManager.stop()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Existing Bodies: Digital Clock, Battery, Notes, Quote, Counter, Mini Calendar, Minimal
// -------------------------------------------------------------
@Composable
private fun DigitalClockBody(config: WidgetConfig, stats: DeviceStats, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stats.currentTimeFormatted,
            color = textColor,
            fontSize = if (config.size == WidgetSize.SMALL_2x2) 34.sp else 44.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = (-1).sp
        )

        if (config.showSeconds) {
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = stats.currentSeconds,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (stats.isAm) "AM" else "PM",
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BatteryDeviceBody(config: WidgetConfig, stats: DeviceStats, textColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Battery",
                    tint = if (stats.isCharging) Color(0xFF34D399) else textColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stats.batteryPct}%",
                    color = textColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                if (stats.isCharging) {
                    Text(
                        text = " (يشحن)",
                        color = Color(0xFF34D399),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "صحة: ${stats.batteryHealth}",
                color = textColor.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val animatedProgress by animateFloatAsState(targetValue = stats.batteryPct / 100f, label = "battery")
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (stats.batteryPct > 20) Color(0xFF10B981) else Color(0xFFEF4444),
            trackColor = Color.White.copy(alpha = 0.2f),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "المساحة المتاحة: ${stats.availableStorageGb} GB من ${stats.totalStorageGb} GB",
            color = textColor.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun QuickNotesBody(config: WidgetConfig, textColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = config.noteContent,
            color = textColor,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            maxLines = if (config.size == WidgetSize.LARGE_4x4) 8 else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DailyQuoteBody(config: WidgetConfig, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.FormatQuote,
            contentDescription = null,
            tint = textColor.copy(alpha = 0.6f),
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp)
        )
        Text(
            text = "«${config.quoteArabic}»",
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HabitCounterBody(
    config: WidgetConfig,
    textColor: Color,
    isInteractive: Boolean,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${config.counterValue}",
                color = textColor,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = config.counterLabel,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        }

        if (isInteractive) {
            ElevatedButton(
                onClick = onIncrement,
                shape = CircleShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor = textColor
                ),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_increment_${config.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increment",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniCalendarBody(stats: DeviceStats, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stats.currentDayNameAr,
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stats.currentDateAr,
                color = textColor.copy(alpha = 0.85f),
                fontSize = 15.sp
            )
        }

        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "اليوم",
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MinimalStatBody(config: WidgetConfig, textColor: Color) {
    Column {
        Text(
            text = config.noteContent,
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AppShortcutsWidgetBody(textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sampleShortcuts = listOf(
            Triple("واتساب", Color(0xFF25D366), false),
            Triple("يوتيوب", Color(0xFFFF0000), false),
            Triple("واتساب 2", Color(0xFF10B981), true),
            Triple("تليجرام", Color(0xFF2AABEE), false)
        )

        sampleShortcuts.forEach { (name, color, isDual) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = color.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, color),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = name.take(1),
                                color = color,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    if (isDual) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        ) {}
                    }
                }

                Text(
                    text = name,
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
