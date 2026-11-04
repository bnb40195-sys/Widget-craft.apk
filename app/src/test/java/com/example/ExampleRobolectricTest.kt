package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.StudioPreferencesRepository
import com.example.data.local.WidgetRepository
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.KeyboardTheme
import com.example.data.model.OfflineDataProvider
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.data.model.WidgetAlert
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetTheme
import com.example.data.model.WidgetType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WidgetCraft", appName)
    }

    @Test
    fun `test widget json export and import`() {
        val original = WidgetConfig(
            name = "ساعة المطر النيونية",
            tagline = "ساعة بدون نت مع الثواني",
            type = WidgetType.DIGITAL_CLOCK,
            theme = WidgetTheme.CYBER_NEON,
            cornerRadiusDp = 26,
            size = WidgetSize.WIDE_4x1,
            showSeconds = true,
            showDate = true
        )

        val jsonString = original.toJsonString()
        assertTrue(jsonString.contains("WidgetCraft"))
        assertTrue(jsonString.contains("ساعة المطر النيونية"))

        val parsedResult = WidgetConfig.fromJsonString(jsonString)
        assertTrue(parsedResult.isSuccess)

        val imported = parsedResult.getOrThrow()
        assertEquals("ساعة المطر النيونية", imported.name)
        assertEquals("ساعة بدون نت مع الثواني", imported.tagline)
        assertEquals(WidgetType.DIGITAL_CLOCK, imported.type)
        assertEquals(WidgetTheme.CYBER_NEON, imported.theme)
        assertEquals(26, imported.cornerRadiusDp)
        assertEquals(WidgetSize.WIDE_4x1, imported.size)
        assertTrue(imported.showSeconds)
        assertEquals("IMPORTED", imported.source)
    }

    @Test
    fun `test offline templates availability`() {
        val templates = OfflineDataProvider.preinstalledTemplates
        assertTrue(templates.isNotEmpty())
        assertTrue(templates.any { it.type == WidgetType.DIGITAL_CLOCK })
        assertTrue(templates.any { it.type == WidgetType.BATTERY_DEVICE })
        assertTrue(templates.any { it.type == WidgetType.HABIT_COUNTER })
        assertTrue(templates.any { it.type == WidgetType.QUICK_NOTES })
    }

    @Test
    fun `test repository initialization and counter increment`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = WidgetRepository(context)

        repository.initializeDefaultsIfNeeded()

        val widgets = repository.allWidgets.first()
        assertTrue(widgets.isNotEmpty())

        val counterWidget = widgets.find { it.type == WidgetType.HABIT_COUNTER }
        if (counterWidget != null) {
            val initialCount = counterWidget.counterValue
            repository.incrementCounter(counterWidget.id)
            val updated = repository.getWidgetById(counterWidget.id)
            assertNotNull(updated)
            assertEquals(initialCount + counterWidget.counterStep, updated!!.counterValue)
        }
    }

    @Test
    fun `test studio preferences repository features`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val studioPrefs = StudioPreferencesRepository(context)

        // 1. Wallpaper test
        val wallpaper = WallpaperConfig(
            type = WallpaperType.PRESET_GRADIENT,
            presetIndex = 2,
            dimLevel = 0.4f
        )
        studioPrefs.setWallpaper(wallpaper)
        assertEquals(2, studioPrefs.wallpaper.value.presetIndex)
        assertEquals(0.4f, studioPrefs.wallpaper.value.dimLevel, 0.01f)

        // 2. Shortcuts & Dual Apps test
        val shortcut = AppShortcut(
            name = "تليجرام للعمل",
            customLabel = "تليجرام 2",
            iconShape = IconShape.HEXAGON,
            isDualApp = true,
            dualLabel = "مزدوج",
            isHidden = false
        )
        studioPrefs.addOrUpdateShortcut(shortcut)
        val loaded = studioPrefs.shortcuts.value.find { it.id == shortcut.id }
        assertNotNull(loaded)
        assertTrue(loaded!!.isDualApp)
        assertEquals(IconShape.HEXAGON, loaded.iconShape)

        // Toggle hide to vault
        studioPrefs.toggleHideShortcut(shortcut.id)
        val hiddenLoaded = studioPrefs.shortcuts.value.find { it.id == shortcut.id }
        assertTrue(hiddenLoaded!!.isHidden)

        // 3. Keyboard Theme JSON test
        val kbTheme = KeyboardTheme(
            name = "سمة الزجاج الماسي",
            backgroundColor = 0xFF121212,
            keyColor = 0xFF222222,
            accentColor = 0xFF38BDF8
        )
        val json = kbTheme.toJsonString()
        val parsedTheme = KeyboardTheme.fromJsonString(json)
        assertTrue(parsedTheme.isSuccess)
        assertEquals("سمة الزجاج الماسي", parsedTheme.getOrThrow().name)

        // 4. Alerts test
        val alert = WidgetAlert(
            title = "وقت شرب الماء",
            hour = 14,
            minute = 15,
            repeatMode = "يومياً"
        )
        studioPrefs.addOrUpdateAlert(alert)
        val alertLoaded = studioPrefs.alerts.value.find { it.id == alert.id }
        assertNotNull(alertLoaded)
        assertEquals(14, alertLoaded!!.hour)
        assertEquals(15, alertLoaded.minute)
    }

    @Test
    fun `test offline islamic prayer calculator for Oran and Makkah`() {
        // Calculate prayers for Oran (Algeria)
        val oranSchedule = com.example.device.IslamicTimeCalculator.calculatePrayers(
            lat = 35.6976,
            lng = -0.6337,
            timeZoneOffsetHours = 1.0,
            hijriOffsetDays = 0
        )
        assertNotNull(oranSchedule.fajr)
        assertNotNull(oranSchedule.dhuhr)
        assertNotNull(oranSchedule.asr)
        assertNotNull(oranSchedule.maghrib)
        assertNotNull(oranSchedule.isha)
        assertTrue(oranSchedule.dateHijriFormatted.contains("144"))
        assertTrue(oranSchedule.timeRemainingFormatted.isNotEmpty())

        // Calculate prayers for Makkah (Saudi Arabia)
        val makkahSchedule = com.example.device.IslamicTimeCalculator.calculatePrayers(
            lat = 21.3891,
            lng = 39.8579,
            timeZoneOffsetHours = 3.0,
            hijriOffsetDays = 0
        )
        assertNotNull(makkahSchedule.fajr)
        assertTrue(makkahSchedule.dayNameArabic.isNotEmpty())
    }

    @Test
    fun `test new widget models json roundtrip`() {
        val prayerWidget = WidgetConfig(
            name = "مواقيت وهران",
            type = WidgetType.PRAYER_TIMES,
            cityName = "وهران (Oran)",
            cityLatitude = 35.6976,
            cityLongitude = -0.6337,
            cityTimeZone = 1.0,
            hijriDayOffset = 1
        )
        val json = prayerWidget.toJsonString()
        val parsed = WidgetConfig.fromJsonString(json)
        assertTrue(parsed.isSuccess)
        val result = parsed.getOrThrow()
        assertEquals(WidgetType.PRAYER_TIMES, result.type)
        assertEquals("وهران (Oran)", result.cityName)
        assertEquals(35.6976, result.cityLatitude, 0.001)
        assertEquals(1, result.hijriDayOffset)

        val quranWidget = WidgetConfig(
            name = "القرآن بصوت",
            type = WidgetType.QURAN_AUDIO,
            quranSurahName = "سورة الملك",
            quranReciterName = "مشاري راشد العفاسي"
        )
        val quranJson = quranWidget.toJsonString()
        val quranParsed = WidgetConfig.fromJsonString(quranJson)
        assertTrue(quranParsed.isSuccess)
        assertEquals("سورة الملك", quranParsed.getOrThrow().quranSurahName)
    }

    @Test
    fun `test offline audio manager surahs availability`() {
        val surahs = com.example.audio.AudioPlaybackManager.availableSurahs
        assertTrue(surahs.isNotEmpty())
        assertTrue(surahs.any { it.name.contains("الفاتحة") })
        assertTrue(surahs.any { it.name.contains("الكرسي") })
        assertTrue(surahs.any { it.name.contains("الإخلاص") })
    }
}
