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
        assertTrue(summaryAr.content.contains("ARNA AI"))
        assertTrue(summaryAr.content.contains("Firebase"))

        val summaryEn = repo.summarizePage(
            "Sample Article",
            "https://example.com",
            "This is a sample paragraph to verify English ARNA AI output.",
            BrowserLanguage.ENGLISH
        )
        assertTrue(summaryEn.isSampleMode)
        assertTrue(summaryEn.content.contains("ARNA AI"))
        assertTrue(summaryEn.content.contains("Key extracted highlights"))

        val translation = repo.translatePage("مقال", "Hello world", "العربية", BrowserLanguage.ARABIC)
        assertTrue(translation.isSampleMode)
        assertTrue(translation.content.contains("ARNA AI"))
        assertTrue(translation.content.contains("العربية"))
    }
}
