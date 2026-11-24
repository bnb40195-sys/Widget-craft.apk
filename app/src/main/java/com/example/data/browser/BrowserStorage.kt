package com.example.data.browser

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

enum class BrowserLanguage(val code: String, val nativeLabel: String) {
    ARABIC("ar", "العربية"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String?): BrowserLanguage =
            entries.firstOrNull { it.code == code } ?: ARABIC
    }
}

enum class BrowserSearchEngine(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val badge: String,
    val searchUrl: String,
    val homeUrl: String
) {
    DUCKDUCKGO("duckduckgo", "Arna (خصوصية)", "Arna (Privacy)", "🛡️", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BING("bing", "Bing Search", "Bing Search", "🔎", "https://www.bing.com/search?q=", "https://www.bing.com"),
    WIKIPEDIA("wikipedia", "ويكيبيديا الموسوعة", "Wikipedia", "📚", "https://ar.wikipedia.org/w/index.php?search=", "https://ar.wikipedia.org"),
    QURAN("quran", "البحث في القرآن", "Quran Search", "📖", "https://quran.com/search?q=", "https://quran.com");

    fun displayTitle(language: BrowserLanguage): String =
        if (language == BrowserLanguage.ENGLISH) titleEn else titleAr

    fun buildSearchUrl(query: String, language: BrowserLanguage): String {
        val encoded = Uri.encode(query)
        return when (this) {
            DUCKDUCKGO -> if (language == BrowserLanguage.ARABIC) {
                "https://duckduckgo.com/?q=$encoded&kl=xa-ar&kad=ar_SA&ia=web"
            } else {
                "https://duckduckgo.com/?q=$encoded&kl=us-en&kad=en_US&ia=web"
            }
            BING -> if (language == BrowserLanguage.ARABIC) {
                "https://www.bing.com/search?q=$encoded&setlang=ar&cc=SA"
            } else {
                "https://www.bing.com/search?q=$encoded&setlang=en&cc=US"
            }
            WIKIPEDIA -> if (language == BrowserLanguage.ARABIC) {
                "https://ar.wikipedia.org/w/index.php?search=$encoded"
            } else {
                "https://en.wikipedia.org/w/index.php?search=$encoded"
            }
            QURAN -> if (language == BrowserLanguage.ARABIC) {
                "https://quran.com/ar/search?q=$encoded"
            } else {
                "https://quran.com/en/search?q=$encoded"
            }
        }
    }

    fun homeUrlFor(language: BrowserLanguage): String = when (this) {
        DUCKDUCKGO -> if (language == BrowserLanguage.ARABIC) {
            "https://duckduckgo.com/?kl=xa-ar&kad=ar_SA"
        } else {
            "https://duckduckgo.com/?kl=us-en&kad=en_US"
        }
        BING -> if (language == BrowserLanguage.ARABIC) {
            "https://www.bing.com/?setlang=ar&cc=SA"
        } else {
            "https://www.bing.com/?setlang=en&cc=US"
        }
        WIKIPEDIA -> if (language == BrowserLanguage.ARABIC) {
            "https://ar.wikipedia.org"
        } else {
            "https://en.wikipedia.org"
        }
        QURAN -> if (language == BrowserLanguage.ARABIC) {
            "https://quran.com/ar"
        } else {
            "https://quran.com/en"
        }
    }

    companion object {
        fun fromId(id: String?): BrowserSearchEngine =
            entries.firstOrNull { it.id == id } ?: DUCKDUCKGO

        fun buildLanguageHeaders(language: BrowserLanguage): Map<String, String> =
            if (language == BrowserLanguage.ARABIC) {
                mapOf("Accept-Language" to "ar-SA,ar;q=0.9,en-US;q=0.4,en;q=0.3")
            } else {
                mapOf("Accept-Language" to "en-US,en;q=0.9")
            }

        fun rewriteSearchUrlForLanguage(url: String, language: BrowserLanguage): String {
            val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return url
            val host = uri.host?.lowercase() ?: return url
            return when {
                host.contains("duckduckgo.com") -> {
                    val kl = if (language == BrowserLanguage.ARABIC) "xa-ar" else "us-en"
                    val kad = if (language == BrowserLanguage.ARABIC) "ar_SA" else "en_US"
                    val builder = uri.buildUpon().clearQuery()
                    for (name in uri.queryParameterNames) {
                        if (name != "kl" && name != "kad") {
                            uri.getQueryParameter(name)?.let { builder.appendQueryParameter(name, it) }
                        }
                    }
                    builder.appendQueryParameter("kl", kl)
                    builder.appendQueryParameter("kad", kad)
                    builder.build().toString()
                }
                host.contains("bing.com") -> {
                    val setlang = if (language == BrowserLanguage.ARABIC) "ar" else "en"
                    val cc = if (language == BrowserLanguage.ARABIC) "SA" else "US"
                    val builder = uri.buildUpon().clearQuery()
                    for (name in uri.queryParameterNames) {
                        if (name != "setlang" && name != "cc") {
                            uri.getQueryParameter(name)?.let { builder.appendQueryParameter(name, it) }
                        }
                    }
                    builder.appendQueryParameter("setlang", setlang)
                    builder.appendQueryParameter("cc", cc)
                    builder.build().toString()
                }
                host.endsWith("wikipedia.org") -> {
                    val targetHost = if (language == BrowserLanguage.ARABIC) "ar.wikipedia.org" else "en.wikipedia.org"
                    uri.buildUpon().authority(targetHost).build().toString()
                }
                else -> url
            }
        }
    }
}

data class SavedBrowserPage(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val savedAt: Long = System.currentTimeMillis()
)

data class BrowserHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

data class BrowserDownloadOperation(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val url: String,
    val mimeType: String,
    val sizeBytes: Long,
    val status: String,
    val downloadManagerId: Long = -1L,
    val localFilePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class BrowserSettingsConfig(
    val language: BrowserLanguage = BrowserLanguage.ARABIC,
    val defaultEngine: BrowserSearchEngine = BrowserSearchEngine.DUCKDUCKGO,
    val javaScriptEnabled: Boolean = true,
    val desktopMode: Boolean = false,
    val privateMode: Boolean = false,
    val blockPopups: Boolean = true,
    val blockThirdPartyCookies: Boolean = true,
    val textZoomPercent: Int = 100
)

class BrowserStorageManager(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("browser_storage_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BOOKMARKS = "bookmarks_json"
        private const val KEY_HISTORY = "history_json"
        private const val KEY_DOWNLOADS = "downloads_json"
        private const val KEY_LANGUAGE = "pref_browser_language"
        private const val KEY_ENGINE = "pref_engine"
        private const val KEY_JS = "pref_js"
        private const val KEY_DESKTOP = "pref_desktop"
        private const val KEY_PRIVATE = "pref_private"
        private const val KEY_BLOCK_POPUPS = "pref_block_popups"
        private const val KEY_BLOCK_COOKIES = "pref_block_third_party_cookies"
        private const val KEY_ZOOM = "pref_text_zoom"
    }

    fun loadSettings(): BrowserSettingsConfig {
        return BrowserSettingsConfig(
            language = BrowserLanguage.fromCode(prefs.getString(KEY_LANGUAGE, BrowserLanguage.ARABIC.code)),
            defaultEngine = BrowserSearchEngine.fromId(prefs.getString(KEY_ENGINE, BrowserSearchEngine.DUCKDUCKGO.id)),
            javaScriptEnabled = prefs.getBoolean(KEY_JS, true),
            desktopMode = prefs.getBoolean(KEY_DESKTOP, false),
            privateMode = prefs.getBoolean(KEY_PRIVATE, false),
            blockPopups = prefs.getBoolean(KEY_BLOCK_POPUPS, true),
            blockThirdPartyCookies = prefs.getBoolean(KEY_BLOCK_COOKIES, true),
            textZoomPercent = prefs.getInt(KEY_ZOOM, 100).coerceIn(50, 200)
        )
    }

    fun saveSettings(config: BrowserSettingsConfig) {
        prefs.edit()
            .putString(KEY_LANGUAGE, config.language.code)
            .putString(KEY_ENGINE, config.defaultEngine.id)
            .putBoolean(KEY_JS, config.javaScriptEnabled)
            .putBoolean(KEY_DESKTOP, config.desktopMode)
            .putBoolean(KEY_PRIVATE, config.privateMode)
            .putBoolean(KEY_BLOCK_POPUPS, config.blockPopups)
            .putBoolean(KEY_BLOCK_COOKIES, config.blockThirdPartyCookies)
            .putInt(KEY_ZOOM, config.textZoomPercent.coerceIn(50, 200))
            .apply()
    }

    fun loadBookmarks(): List<SavedBrowserPage> {
        val raw = prefs.getString(KEY_BOOKMARKS, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        SavedBrowserPage(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = obj.optString("title", "Saved Page"),
                            url = obj.optString("url", ""),
                            savedAt = obj.optLong("savedAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveBookmarks(list: List<SavedBrowserPage>) {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("url", item.url)
                    put("savedAt", item.savedAt)
                }
            )
        }
        prefs.edit().putString(KEY_BOOKMARKS, arr.toString()).apply()
    }

    fun toggleBookmark(title: String, url: String): Pair<List<SavedBrowserPage>, Boolean> {
        if (url.isBlank() || url == "about:blank") return loadBookmarks() to false
        val current = loadBookmarks().toMutableList()
        val existingIdx = current.indexOfFirst { it.url.equals(url, ignoreCase = true) }
        return if (existingIdx >= 0) {
            current.removeAt(existingIdx)
            saveBookmarks(current)
            current to false
        } else {
            val cleanTitle = title.ifBlank { url }
            current.add(0, SavedBrowserPage(title = cleanTitle, url = url))
            saveBookmarks(current)
            current to true
        }
    }

    fun removeBookmark(id: String): List<SavedBrowserPage> {
        val updated = loadBookmarks().filterNot { it.id == id }
        saveBookmarks(updated)
        return updated
    }

    fun loadHistory(): List<BrowserHistoryItem> {
        val raw = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        BrowserHistoryItem(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = obj.optString("title", "Web Page"),
                            url = obj.optString("url", ""),
                            visitedAt = obj.optLong("visitedAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveHistory(list: List<BrowserHistoryItem>) {
        val arr = JSONArray()
        list.take(150).forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("url", item.url)
                    put("visitedAt", item.visitedAt)
                }
            )
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    fun addHistoryEntry(title: String, url: String): List<BrowserHistoryItem> {
        if (url.isBlank() || url == "about:blank") return loadHistory()
        val current = loadHistory().toMutableList()
        current.removeAll { it.url == url && (System.currentTimeMillis() - it.visitedAt) < 15_000 }
        current.add(
            0,
            BrowserHistoryItem(
                title = title.ifBlank { url },
                url = url,
                visitedAt = System.currentTimeMillis()
            )
        )
        saveHistory(current)
        return current.take(150)
    }

    fun removeHistoryEntry(id: String): List<BrowserHistoryItem> {
        val updated = loadHistory().filterNot { it.id == id }
        saveHistory(updated)
        return updated
    }

    fun clearHistory(): List<BrowserHistoryItem> {
        saveHistory(emptyList())
        return emptyList()
    }

    fun loadDownloads(): List<BrowserDownloadOperation> {
        val raw = prefs.getString(KEY_DOWNLOADS, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        BrowserDownloadOperation(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            fileName = obj.optString("fileName", "download"),
                            url = obj.optString("url", ""),
                            mimeType = obj.optString("mimeType", "application/octet-stream"),
                            sizeBytes = obj.optLong("sizeBytes", 0L),
                            status = obj.optString("status", "Completed"),
                            downloadManagerId = obj.optLong("downloadManagerId", -1L),
                            localFilePath = obj.optString("localFilePath", "").ifBlank { null },
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveDownloads(list: List<BrowserDownloadOperation>) {
        val arr = JSONArray()
        list.take(100).forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("fileName", item.fileName)
                    put("url", item.url)
                    put("mimeType", item.mimeType)
                    put("sizeBytes", item.sizeBytes)
                    put("status", item.status)
                    put("downloadManagerId", item.downloadManagerId)
                    put("localFilePath", item.localFilePath ?: "")
                    put("createdAt", item.createdAt)
                }
            )
        }
        prefs.edit().putString(KEY_DOWNLOADS, arr.toString()).apply()
    }

    fun enqueueSystemDownload(
        url: String,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): Result<List<BrowserDownloadOperation>> {
        return try {
            val guessedName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                .ifBlank { "file_${System.currentTimeMillis()}" }
            val resolvedMime = mimeType?.ifBlank { "application/octet-stream" } ?: "application/octet-stream"
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(guessedName)
                setDescription(
                    if (language == BrowserLanguage.ENGLISH) "Downloading from Arna Browser"
                    else "جاري التنزيل من متصفح Arna"
                )
                setMimeType(resolvedMime)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessedName)
            }
            val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val dmId = dm.enqueue(request)
            val op = BrowserDownloadOperation(
                fileName = guessedName,
                url = url,
                mimeType = resolvedMime,
                sizeBytes = contentLength.coerceAtLeast(0L),
                status = if (language == BrowserLanguage.ENGLISH) "Downloading" else "جاري التنزيل",
                downloadManagerId = dmId
            )
            val updated = listOf(op) + loadDownloads()
            saveDownloads(updated)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveHtmlPageLocally(
        title: String,
        url: String,
        htmlOrTextContent: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): Result<List<BrowserDownloadOperation>> {
        return try {
            val safeTitle = title.replace(Regex("[^\\p{L}\\p{N}_\\- ]"), "").trim()
                .take(40)
                .ifBlank { "webpage" }
                .replace(" ", "_")
            val fileName = "${safeTitle}_${System.currentTimeMillis() % 100000}.html"
            val dir = File(appContext.filesDir, "saved_pages").apply { mkdirs() }
            val outFile = File(dir, fileName)
            outFile.writeText(htmlOrTextContent, Charsets.UTF_8)

            val op = BrowserDownloadOperation(
                fileName = fileName,
                url = url,
                mimeType = "text/html",
                sizeBytes = outFile.length(),
                status = if (language == BrowserLanguage.ENGLISH) "Saved on device" else "محفوظ في الجهاز",
                localFilePath = outFile.absolutePath
            )
            val updated = listOf(op) + loadDownloads()
            saveDownloads(updated)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun refreshDownloadStatuses(): List<BrowserDownloadOperation> {
        val current = loadDownloads()
        val activeIds = current
            .filter { it.downloadManagerId > 0L && (it.status == "Downloading" || it.status == "جاري التنزيل") }
            .map { it.downloadManagerId }
        if (activeIds.isEmpty()) return current

        val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return current
        val byDmId = current.associateBy { it.downloadManagerId }
        val updated = mutableMapOf<Long, BrowserDownloadOperation>()

        runCatching {
            dm.query(DownloadManager.Query().setFilterById(*activeIds.toLongArray()))?.use { cursor ->
                val colId = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                val colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val colBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val colTotal = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val colUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                while (cursor.moveToNext()) {
                    val dmId = if (colId >= 0) cursor.getLong(colId) else -1L
                    val orig = byDmId[dmId] ?: continue
                    val st = if (colStatus >= 0) cursor.getInt(colStatus) else DownloadManager.STATUS_PENDING
                    val bytes = if (colBytes >= 0) cursor.getLong(colBytes).coerceAtLeast(0L) else orig.sizeBytes
                    val total = if (colTotal >= 0) cursor.getLong(colTotal).coerceAtLeast(0L) else orig.sizeBytes
                    val uri = if (colUri >= 0) cursor.getString(colUri) else orig.localFilePath
                    val label = when (st) {
                        DownloadManager.STATUS_SUCCESSFUL -> "مكتمل 100%"
                        DownloadManager.STATUS_FAILED -> "فشل التنزيل"
                        DownloadManager.STATUS_PAUSED -> "متوقف مؤقتاً"
                        DownloadManager.STATUS_RUNNING -> if (total > 0L) "${((bytes * 100) / total).coerceIn(0, 100)}%" else "جاري التنزيل"
                        else -> orig.status
                    }
                    updated[dmId] = orig.copy(
                        sizeBytes = if (total > 0L) total else bytes,
                        status = label,
                        localFilePath = uri ?: orig.localFilePath
                    )
                }
            }
        }
        if (updated.isEmpty()) return current
        val merged = current.map { updated[it.downloadManagerId] ?: it }
        saveDownloads(merged)
        return merged
    }

    fun removeDownloadOperation(id: String): List<BrowserDownloadOperation> {
        val current = loadDownloads()
        val target = current.firstOrNull { it.id == id }
        target?.localFilePath?.let { path ->
            runCatching { File(path).delete() }
        }
        val updated = current.filterNot { it.id == id }
        saveDownloads(updated)
        return updated
    }

    fun clearDownloads(): List<BrowserDownloadOperation> {
        loadDownloads().forEach { item ->
            item.localFilePath?.let { path -> runCatching { File(path).delete() } }
        }
        saveDownloads(emptyList())
        return emptyList()
    }
}
