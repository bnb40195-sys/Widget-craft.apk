package com.example.device

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class PrayerSchedule(
    val cityName: String,
    val dateGregorianFormatted: String,
    val dayNameArabic: String,
    val dateHijriFormatted: String,
    val hijriDay: Int,
    val hijriMonthNameArabic: String,
    val hijriYear: Int,
    val gregorianDay: Int,
    val gregorianMonthNameArabic: String,
    val gregorianYear: Int,
    val fajr: String,
    val shurooq: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val timeRemainingFormatted: String, // e.g. "1 س 2 دقيقة حتى المغرب @ 18:19"
    val currentPrayerActive: String
) {
    val nextPrayerNameAr: String get() = nextPrayerName
    val nextPrayerCountdown: String get() = timeRemainingFormatted
    val hijriFormatted: String get() = dateHijriFormatted
}

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneOffsetHours: Double
)

object IslamicTimeCalculator {

    fun calculatePrayers(
        lat: Double,
        lng: Double,
        timeZoneOffsetHours: Double,
        hijriOffsetDays: Int = 0
    ): PrayerSchedule {
        val city = CityLocation("موقع مخصص", "", lat, lng, timeZoneOffsetHours)
        return calculatePrayerTimes(Calendar.getInstance(), city, hijriOffsetDays)
    }

    val popularCities = listOf(
        CityLocation("وهران (Oran)", "الجزائر", 35.6976, -0.6337, 1.0),
        CityLocation("الجزائر العاصمة (Algiers)", "الجزائر", 36.7538, 3.0588, 1.0),
        CityLocation("قسنطينة (Constantine)", "الجزائر", 36.3650, 6.6147, 1.0),
        CityLocation("عنابة (Annaba)", "الجزائر", 36.9000, 7.7667, 1.0),
        CityLocation("الرياض (Riyadh)", "السعودية", 24.7136, 46.6753, 3.0),
        CityLocation("مكة المكرمة (Makkah)", "السعودية", 21.3891, 39.8579, 3.0),
        CityLocation("المدينة المنورة (Madinah)", "السعودية", 24.5247, 39.5692, 3.0),
        CityLocation("القاهرة (Cairo)", "مصر", 30.0444, 31.2357, 2.0),
        CityLocation("الدار البيضاء (Casablanca)", "المغرب", 33.5731, -7.5898, 1.0),
        CityLocation("تونس (Tunis)", "تونس", 36.8065, 10.1815, 1.0),
        CityLocation("دبي (Dubai)", "الإمارات", 25.2048, 55.2708, 4.0),
        CityLocation("الدوحة (Doha)", "قطر", 25.2854, 51.5310, 3.0),
        CityLocation("الكويت (Kuwait City)", "الكويت", 29.3759, 47.9774, 3.0),
        CityLocation("عَمّان (Amman)", "الأردن", 31.9454, 35.9284, 3.0),
        CityLocation("القدس الشريف (Jerusalem)", "فلسطين", 31.7683, 35.2137, 2.0),
        CityLocation("بغداد (Baghdad)", "العراق", 33.3152, 44.3661, 3.0),
        CityLocation("مسقط (Muscat)", "عمان", 23.5880, 58.3829, 4.0)
    )

    fun calculatePrayerTimes(
        calendar: Calendar = Calendar.getInstance(),
        city: CityLocation = popularCities[0],
        hijriOffsetDays: Int = 0
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val lat = city.latitude
        val lng = city.longitude
        val tz = city.timeZoneOffsetHours

        // Astronomical Julian Date calculation
        val a = floor((14 - month) / 12.0)
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jd = day + floor((153 * m + 2) / 5.0) + 365 * y + floor(y / 4.0) - floor(y / 100.0) + floor(y / 400.0) - 32045

        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(degToRad(g)) + 0.020 * sin(degToRad(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(radToDeg(atan(cos(degToRad(e)) * tan(degToRad(l))))) / 15.0
        val dec = radToDeg(asin(sin(degToRad(e)) * sin(degToRad(l))))
        val eqt = q / 15.0 - ra

        // Solar Noon (Dhuhr)
        val noon = fixHour(12.0 + tz - lng / 15.0 - eqt)

        // Sun angles for Fajr (18°), Sunrise (0.833°), Sunset (0.833°), Isha (17.5°)
        val fajrDiff = sunAngleDiff(lat, dec, -18.0)
        val sunriseDiff = sunAngleDiff(lat, dec, -0.833)
        val ishaDiff = sunAngleDiff(lat, dec, -17.5)

        // Asr: shadow factor = 1 (Shafi / Standard)
        val asrAngle = radToDeg(atan(1.0 / (1.0 + tan(degToRad(Math.abs(lat - dec))))))
        val asrDiff = sunAngleDiff(lat, dec, asrAngle)

        val fajrTime = fixHour(noon - fajrDiff)
        val shurooqTime = fixHour(noon - sunriseDiff)
        val dhuhrTime = noon + (2.0 / 60.0) // 2 mins safety
        val asrTime = fixHour(noon + asrDiff)
        val maghribTime = fixHour(noon + sunriseDiff + (2.0 / 60.0)) // Sunset + 2 mins
        val ishaTime = fixHour(noon + ishaDiff)

        val fajrStr = formatTime(fajrTime)
        val shurooqStr = formatTime(shurooqTime)
        val dhuhrStr = formatTime(dhuhrTime)
        val asrStr = formatTime(asrTime)
        val maghribStr = formatTime(maghribTime)
        val ishaStr = formatTime(ishaTime)

        // Calculate Hijri Date
        val hijriDate = calculateHijriDate(year, month, day, hijriOffsetDays)

        // Determine current and next prayer
        val currentHourDec = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0

        val (nextName, nextTimeDec, nextTimeStr) = when {
            currentHourDec < fajrTime -> Triple("الفجر", fajrTime, fajrStr)
            currentHourDec < shurooqTime -> Triple("الشروق", shurooqTime, shurooqStr)
            currentHourDec < dhuhrTime -> Triple("الظهر", dhuhrTime, dhuhrStr)
            currentHourDec < asrTime -> Triple("العصر", asrTime, asrStr)
            currentHourDec < maghribTime -> Triple("المغرب", maghribTime, maghribStr)
            currentHourDec < ishaTime -> Triple("العشاء", ishaTime, ishaStr)
            else -> Triple("الفجر", fajrTime + 24.0, fajrStr) // Next day Fajr
        }

        val activeName = when {
            currentHourDec < fajrTime -> "العشاء"
            currentHourDec < shurooqTime -> "الفجر"
            currentHourDec < dhuhrTime -> "الشروق"
            currentHourDec < asrTime -> "الظهر"
            currentHourDec < maghribTime -> "العصر"
            currentHourDec < ishaTime -> "المغرب"
            else -> "العشاء"
        }

        // Remaining time calculation
        val diffMinutesTotal = (((nextTimeDec - currentHourDec) * 60.0) + 1440.0).toInt() % 1440
        val remHours = diffMinutesTotal / 60
        val remMins = diffMinutesTotal % 60
        val remainingFormatted = "${remHours} س ${remMins} دقيقة حتى $nextName @ $nextTimeStr"

        val arabicDayFormat = SimpleDateFormat("EEEE", Locale("ar"))
        val gregorianMonthFormat = SimpleDateFormat("MMMM", Locale("ar"))
        val fullGregorianFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))

        return PrayerSchedule(
            cityName = "${city.name.substringBefore(" (")}, ${city.country}",
            dateGregorianFormatted = fullGregorianFormat.format(calendar.time),
            dayNameArabic = arabicDayFormat.format(calendar.time),
            dateHijriFormatted = "${hijriDate.day} ${hijriDate.monthName} ${hijriDate.year}",
            hijriDay = hijriDate.day,
            hijriMonthNameArabic = hijriDate.monthName,
            hijriYear = hijriDate.year,
            gregorianDay = day,
            gregorianMonthNameArabic = gregorianMonthFormat.format(calendar.time),
            gregorianYear = year,
            fajr = fajrStr,
            shurooq = shurooqStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            nextPrayerName = nextName,
            nextPrayerTime = nextTimeStr,
            timeRemainingFormatted = remainingFormatted,
            currentPrayerActive = activeName
        )
    }

    private fun sunAngleDiff(lat: Double, dec: Double, angle: Double): Double {
        val cosH = (sin(degToRad(angle)) - sin(degToRad(lat)) * sin(degToRad(dec))) /
                (cos(degToRad(lat)) * cos(degToRad(dec)))
        val clamped = cosH.coerceIn(-1.0, 1.0)
        return radToDeg(acos(clamped)) / 15.0
    }

    private fun formatTime(hourDec: Double): String {
        val normalized = fixHour(hourDec)
        val h = floor(normalized).toInt()
        val m = floor((normalized - h) * 60.0).toInt()
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    private fun degToRad(deg: Double): Double = deg * Math.PI / 180.0
    private fun radToDeg(rad: Double): Double = rad * 180.0 / Math.PI
    private fun fixAngle(angle: Double): Double = (angle % 360.0 + 360.0) % 360.0
    private fun fixHour(hour: Double): Double = (hour % 24.0 + 24.0) % 24.0

    // Offline Hijri Date Algorithm
    private data class HijriDateResult(val day: Int, val monthName: String, val year: Int)

    private val hijriMonths = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    private fun calculateHijriDate(year: Int, month: Int, day: Int, offsetDays: Int): HijriDateResult {
        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5 + offsetDays

        val z = jd - 1948440.0 + 10632.0
        val n = floor((z - 1) / 10631.0)
        val zPrime = z - 10631.0 * n + 354.0
        val j = (floor((10985.0 - zPrime) / 5316.0)) * (floor((50.0 * zPrime) / 17719.0)) +
                (floor(zPrime / 5670.0)) * (floor((43.0 * zPrime) / 15238.0))
        val zDoublePrime = zPrime - (floor((30.0 - j) / 15.0)) * (floor((17719.0 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238.0 * j) / 43.0)) + 29
        val mPrime = floor((24.0 * zDoublePrime) / 709.0).toInt()
        val hDay = (zDoublePrime - floor((709.0 * mPrime) / 24.0)).toInt()
        val hMonth = mPrime
        val hYear = (30 * n + j - 30).toInt()

        val safeMonthIndex = (hMonth - 1).coerceIn(0, 11)
        val monthName = hijriMonths[safeMonthIndex]

        return HijriDateResult(day = hDay.coerceIn(1, 30), monthName = monthName, year = hYear)
    }
}
