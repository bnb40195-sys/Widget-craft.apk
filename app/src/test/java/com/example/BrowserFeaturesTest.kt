package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.PlaceholderBrowserAiRepository
import com.example.data.browser.BrowserLanguage
import com.example.data.browser.BrowserSearchEngine
import com.example.data.browser.BrowserSettingsConfig
import com.example.data.browser.BrowserStorageManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BrowserFeaturesTest {

    @Test
    fun `browser storage persists bookmarks history downloads zoom and language settings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = BrowserStorageManager(context)

        // Settings, Language & Zoom
        val updatedSettings = BrowserSettingsConfig(
            language = BrowserLanguage.ENGLISH,
            defaultEngine = BrowserSearchEngine.BING,
            javaScriptEnabled = true,
            desktopMode = true,
            privateMode = true,
            textZoomPercent = 130
        )
        storage.saveSettings(updatedSettings)
        val loadedSettings = storage.loadSettings()
        assertEquals(BrowserLanguage.ENGLISH, loadedSettings.language)
        assertEquals(BrowserSearchEngine.BING, loadedSettings.defaultEngine)
        assertEquals(130, loadedSettings.textZoomPercent)
        assertTrue(loadedSettings.desktopMode)
        assertTrue(loadedSettings.privateMode)

        // Bookmarks toggle
        val (addedList, added) = storage.toggleBookmark("موسوعة", "https://ar.wikipedia.org")
        assertTrue(added)
        assertEquals(1, addedList.size)
        val (removedList, stillPresent) = storage.toggleBookmark("موسوعة", "https://ar.wikipedia.org")
        assertFalse(stillPresent)
        assertTrue(removedList.isEmpty())

        // History
        val history = storage.addHistoryEntry("ويكيبيديا", "https://ar.wikipedia.org")
        assertEquals(1, history.size)
        assertTrue(storage.clearHistory().isEmpty())

        // Save HTML download operation
        val dlResult = storage.saveHtmlPageLocally(
            "صفحة اختبار",
            "https://example.com",
            "<html><body>Test</body></html>",
            BrowserLanguage.ENGLISH
        )
        assertTrue(dlResult.isSuccess)
        assertEquals(1, dlResult.getOrThrow().size)
        assertEquals("Saved on device", dlResult.getOrThrow().first().status)
        assertTrue(storage.clearDownloads().isEmpty())
        assertEquals("Arna (Privacy)", BrowserSearchEngine.DUCKDUCKGO.displayTitle(BrowserLanguage.ENGLISH))
        assertEquals("Arna (خصوصية)", BrowserSearchEngine.DUCKDUCKGO.displayTitle(BrowserLanguage.ARABIC))
    }

    @Test
    fun `placeholder arna ai repository returns bilingual sample labeled output`() = runBlocking {
        val repo = PlaceholderBrowserAiRepository()
        assertFalse(repo.isLiveAiEnabled)

        val summaryAr = repo.summarizePage(
            "مقال تجريبي",
            "https://example.com",
            "هذه جملة طويلة لاختبار التلخيص المحلي في التطبيق.",
            BrowserLanguage.ARABIC
        )
        assertTrue(summaryAr.isSampleMode)
        assertTrue(summaryAr.content.contains("Arna ai"))
        assertTrue(summaryAr.content.contains("Firebase"))

        val summaryEn = repo.summarizePage(
            "Sample Article",
            "https://example.com",
            "This is a sample paragraph to verify English Arna ai output.",
            BrowserLanguage.ENGLISH
        )
        assertTrue(summaryEn.isSampleMode)
        assertTrue(summaryEn.content.contains("Arna ai"))
        assertTrue(summaryEn.content.contains("Key extracted highlights"))

        val translation = repo.translatePage("مقال", "Hello world", "العربية", BrowserLanguage.ARABIC)
        assertTrue(translation.isSampleMode)
        assertTrue(translation.content.contains("Arna ai"))
        assertTrue(translation.content.contains("العربية"))
    }

    @Test
    fun `search engines build and rewrite language-specific urls and headers`() {
        val ddgAr = BrowserSearchEngine.DUCKDUCKGO.buildSearchUrl("Facebook", BrowserLanguage.ARABIC)
        assertTrue(ddgAr.contains("kl=xa-ar"))
        assertTrue(ddgAr.contains("kad=ar_SA"))

        val ddgEn = BrowserSearchEngine.DUCKDUCKGO.buildSearchUrl("Facebook", BrowserLanguage.ENGLISH)
        assertTrue(ddgEn.contains("kl=us-en"))
        assertTrue(ddgEn.contains("kad=en_US"))

        val rewrittenAr = BrowserSearchEngine.rewriteSearchUrlForLanguage(
            "https://duckduckgo.com/?q=Facebook&ia=web",
            BrowserLanguage.ARABIC
        )
        assertTrue(rewrittenAr.contains("kl=xa-ar"))
        assertTrue(rewrittenAr.contains("kad=ar_SA"))

        val wikiAr = BrowserSearchEngine.WIKIPEDIA.buildSearchUrl("Android", BrowserLanguage.ARABIC)
        assertTrue(wikiAr.startsWith("https://ar.wikipedia.org"))
        val wikiEn = BrowserSearchEngine.WIKIPEDIA.buildSearchUrl("Android", BrowserLanguage.ENGLISH)
        assertTrue(wikiEn.startsWith("https://en.wikipedia.org"))

        val headersAr = BrowserSearchEngine.buildLanguageHeaders(BrowserLanguage.ARABIC)
        assertTrue(headersAr["Accept-Language"]?.startsWith("ar") == true)
    }

    @Test
    fun `studio repository persists appLanguage and shortcut iconStyle`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = com.example.data.local.StudioPreferencesRepository(context)
        prefs.setAppLanguage(BrowserLanguage.ENGLISH)
        assertEquals(BrowserLanguage.ENGLISH, prefs.appLanguage.value)
        prefs.setAppLanguage(BrowserLanguage.ARABIC)
        assertEquals(BrowserLanguage.ARABIC, prefs.appLanguage.value)

        val customShortcut = com.example.data.model.AppShortcut(
            id = "test_ayah_tile",
            name = "آية ويدجت",
            customLabel = "آية ويدجت",
            iconColor = 0xFF2D6FE4L,
            iconSymbol = "Ayah",
            iconStyle = "OUTLINE_ROUNDED",
            isActive = true,
            layerIndex = 1
        )
        prefs.addOrUpdateShortcut(customShortcut)
        val reloaded = prefs.shortcuts.value.firstOrNull { it.id == "test_ayah_tile" }
        assertEquals("OUTLINE_ROUNDED", reloaded?.iconStyle)
        assertEquals("Ayah", reloaded?.iconSymbol)
    }

    @Test
    fun `video quality catalog covers 144p to 12K and real download store has no fake entries`() {
        val ids = com.example.data.downloads.VideoQualityCatalog.allVideoQualities.map { it.id }
        assertTrue(ids.contains("auto"))
        assertTrue(ids.contains("144p"))
        assertTrue(ids.contains("240p"))
        assertTrue(ids.contains("360p"))
        assertTrue(ids.contains("480p"))
        assertTrue(ids.contains("720p"))
        assertTrue(ids.contains("1080p"))
        assertTrue(ids.contains("1440p"))
        assertTrue(ids.contains("2160p"))
        assertTrue(ids.contains("4320p"))
        assertTrue(ids.contains("6480p"))

        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = com.example.data.downloads.RealDownloadStore(context)
        assertTrue(store.loadRecords().isEmpty())
        val invalid = store.startRealDownload(
            "not-a-valid-url",
            com.example.data.downloads.MediaDownloadCategory.VIDEO,
            "1080p"
        )
        assertTrue(invalid.isFailure)
    }

    @Test
    fun `arna ai create generate edit modes and wallpaper ai repository generate valid outputs`(): Unit = runBlocking {
        val aiRepo = PlaceholderBrowserAiRepository()
        val createRes = aiRepo.runUniversalTask(
            mode = com.example.data.ai.BrowserAiTaskMode.CREATE,
            prompt = "خطة دراسة أسبوعية",
            pageTitle = "الرئيسية",
            pageUrl = "about:blank",
            pageExcerpt = "",
            language = BrowserLanguage.ARABIC
        )
        assertTrue(createRes.isSampleMode)
        assertTrue(createRes.content.contains("Arna ai"))

        val genRes = aiRepo.runUniversalTask(
            mode = com.example.data.ai.BrowserAiTaskMode.GENERATE,
            prompt = "Generate Kotlin helper",
            pageTitle = "Docs",
            pageUrl = "https://example.com",
            pageExcerpt = "",
            language = BrowserLanguage.ENGLISH
        )
        assertTrue(genRes.isSampleMode)
        assertTrue(genRes.content.contains("Generate Anything"))

        val editRes = aiRepo.runUniversalTask(
            mode = com.example.data.ai.BrowserAiTaskMode.EDIT,
            prompt = "تحسين الصياغة",
            sourceContentToEdit = "نص أولي للتعديل",
            pageTitle = "محرر",
            pageUrl = "about:blank",
            pageExcerpt = "",
            language = BrowserLanguage.ARABIC
        )
        assertTrue(editRes.isSampleMode)
        assertTrue(editRes.content.contains("نص أولي للتعديل"))

        val context = ApplicationProvider.getApplicationContext<Context>()
        val wallpaperRepo = com.example.data.ai.PlaceholderWallpaperAiRepository()
        val preset = com.example.data.ai.wallpaperAiPresets.first()
        val wpResult = wallpaperRepo.generateWallpaper(context, "زخرفة ذهبية هادئة", preset)
        assertTrue(wpResult.isSampleMode)
        val outFile = java.io.File(wpResult.imageFilePath)
        assertTrue(outFile.exists())
        assertTrue(outFile.length() > 1000L)
        outFile.delete()
    }
}
