package com.example.data.downloads

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

enum class MediaDownloadCategory(val labelAr: String, val extension: String) {
    VIDEO("فيديو", "mp4"),
    AUDIO("صوتيات", "mp3"),
    APP_APK("تطبيق APK", "apk"),
    DOCUMENT("ملف ومستند", "bin")
}

data class MediaQualitySpec(
    val id: String,
    val title: String,
    val subtitle: String,
    val maxWidth: Int,
    val maxHeight: Int
)

enum class RealDownloadState {
    PENDING,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class RealDownloadRecord(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val originalUrl: String,
    val category: MediaDownloadCategory,
    val quality: String,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val state: RealDownloadState = RealDownloadState.PENDING,
    val statusText: String = "في قائمة الانتظار...",
    val errorDetail: String? = null,
    val downloadManagerId: Long = -1L,
    val localUri: String? = null,
    val localFilePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = when {
            state == RealDownloadState.COMPLETED -> 1f
            totalBytes > 0L -> (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
            else -> 0f
        }
}

object VideoQualityCatalog {
    val allVideoQualities = listOf(
        MediaQualitySpec("auto", "Auto (تلقائي)", "تكيف تلقائي ذكي", Int.MAX_VALUE, Int.MAX_VALUE),
        MediaQualitySpec("144p", "144p", "توفير فائق (256×144)", 256, 144),
        MediaQualitySpec("240p", "240p", "منخفضة (426×240)", 426, 240),
        MediaQualitySpec("360p", "360p", "اقتصادية (640×360)", 640, 360),
        MediaQualitySpec("480p", "480p SD", "قياسية (854×480)", 854, 480),
        MediaQualitySpec("720p", "720p HD", "عالية الوضوح", 1280, 720),
        MediaQualitySpec("1080p", "1080p FHD", "دقة كاملة", 1920, 1080),
        MediaQualitySpec("1440p", "1440p 2K", "QHD فائقة", 2560, 1440),
        MediaQualitySpec("2160p", "2160p 4K", "Ultra HD 4K", 3840, 2160),
        MediaQualitySpec("4320p", "4320p 8K", "8K FUHD", 7680, 4320),
        MediaQualitySpec("6480p", "6480p 12K", "12K فائقة جداً", 12288, 6480)
    )

    val audioQualities = listOf(
        MediaQualitySpec("320k", "320kbps MP3", "أعلى نقاء صوتي", 0, 0),
        MediaQualitySpec("192k", "192kbps MP3", "جودة متوازنة", 0, 0),
        MediaQualitySpec("128k", "128kbps M4A", "حجم خفيف", 0, 0)
    )

    val apkQualities = listOf(
        MediaQualitySpec("apk_orig", "الحزمة الأصلية APK", "ملف تثبيت مباشر", 0, 0)
    )

    val documentQualities = listOf(
        MediaQualitySpec("orig", "الملف الأصلي", "بدون ضغط", 0, 0)
    )
}

class RealDownloadStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("real_media_downloads_prefs", Context.MODE_PRIVATE)
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    fun loadRecords(): List<RealDownloadRecord> {
        val raw = prefs.getString(KEY_RECORDS, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val category = runCatching {
                        MediaDownloadCategory.valueOf(o.optString("category", MediaDownloadCategory.VIDEO.name))
                    }.getOrDefault(MediaDownloadCategory.VIDEO)
                    val state = runCatching {
                        RealDownloadState.valueOf(o.optString("state", RealDownloadState.PENDING.name))
                    }.getOrDefault(RealDownloadState.PENDING)
                    add(
                        RealDownloadRecord(
                            id = o.optString("id", UUID.randomUUID().toString()),
                            fileName = o.optString("fileName", "download"),
                            originalUrl = o.optString("originalUrl", ""),
                            category = category,
                            quality = o.optString("quality", "1080p"),
                            downloadedBytes = o.optLong("downloadedBytes", 0L),
                            totalBytes = o.optLong("totalBytes", 0L),
                            state = state,
                            statusText = o.optString("statusText", ""),
                            errorDetail = o.optString("errorDetail", "").ifBlank { null },
                            downloadManagerId = o.optLong("downloadManagerId", -1L),
                            localUri = o.optString("localUri", "").ifBlank { null },
                            localFilePath = o.optString("localFilePath", "").ifBlank { null },
                            timestamp = o.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveRecords(records: List<RealDownloadRecord>) {
        val arr = JSONArray()
        records.take(100).forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("fileName", r.fileName)
                    put("originalUrl", r.originalUrl)
                    put("category", r.category.name)
                    put("quality", r.quality)
                    put("downloadedBytes", r.downloadedBytes)
                    put("totalBytes", r.totalBytes)
                    put("state", r.state.name)
                    put("statusText", r.statusText)
                    put("errorDetail", r.errorDetail ?: "")
                    put("downloadManagerId", r.downloadManagerId)
                    put("localUri", r.localUri ?: "")
                    put("localFilePath", r.localFilePath ?: "")
                    put("timestamp", r.timestamp)
                }
            )
        }
        prefs.edit().putString(KEY_RECORDS, arr.toString()).apply()
    }

    fun startRealDownload(
        url: String,
        category: MediaDownloadCategory,
        qualityLabel: String
    ): Result<RealDownloadRecord> {
        val cleanUrl = url.trim()
        if (!cleanUrl.startsWith("http://", ignoreCase = true) &&
            !cleanUrl.startsWith("https://", ignoreCase = true)
        ) {
            return Result.failure(IllegalArgumentException("يرجى إدخال رابط مباشر صالح يبدأ بـ http:// أو https://"))
        }

        val guessed = URLUtil.guessFileName(cleanUrl, null, null)
            .takeIf { it.isNotBlank() && it != "downloadfile.bin" }
            ?: "arna_${category.name.lowercase()}_${System.currentTimeMillis() % 100000}.${category.extension}"
        val fileName = if (guessed.contains('.')) guessed else "$guessed.${category.extension}"

        return try {
            val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(cleanUrl)).apply {
                setTitle(fileName)
                setDescription("جاري التنزيل ($qualityLabel)")
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }
            val dmId = dm.enqueue(request)
            val record = RealDownloadRecord(
                fileName = fileName,
                originalUrl = cleanUrl,
                category = category,
                quality = qualityLabel,
                state = RealDownloadState.PENDING,
                statusText = "جاري الاتصال بالخادم...",
                downloadManagerId = dmId
            )
            val updated = listOf(record) + loadRecords()
            saveRecords(updated)
            Result.success(record)
        } catch (e: Exception) {
            // If system DownloadManager is unavailable on device/sandbox, fall back to direct OkHttp app-storage download
            val record = RealDownloadRecord(
                fileName = fileName,
                originalUrl = cleanUrl,
                category = category,
                quality = qualityLabel,
                state = RealDownloadState.RUNNING,
                statusText = "جاري التنزيل المباشر...",
                downloadManagerId = -1L
            )
            val updated = listOf(record) + loadRecords()
            saveRecords(updated)
            Result.success(record)
        }
    }

    suspend fun streamDirectHttpDownload(
        recordId: String,
        url: String,
        fileName: String,
        onProgress: (downloaded: Long, total: Long) -> Unit
    ): RealDownloadRecord = withContext(Dispatchers.IO) {
        val current = loadRecords().firstOrNull { it.id == recordId }
            ?: return@withContext RealDownloadRecord(
                id = recordId,
                fileName = fileName,
                originalUrl = url,
                category = MediaDownloadCategory.DOCUMENT,
                quality = "الأصلي",
                state = RealDownloadState.FAILED,
                statusText = "تعذر العثور على المهمة"
            )
        val outDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(appContext.filesDir, "downloads")
        outDir.mkdirs()
        val outFile = File(outDir, fileName)

        try {
            val req = Request.Builder().url(url).build()
            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    val failed = current.copy(
                        state = RealDownloadState.FAILED,
                        statusText = "فشل من الخادم (HTTP ${resp.code})",
                        errorDetail = "HTTP ${resp.code}"
                    )
                    updateRecordInPrefs(failed)
                    return@withContext failed
                }
                val body = resp.body ?: throw IllegalStateException("الاستجابة فارغة")
                val total = body.contentLength().coerceAtLeast(0L)
                var downloaded = 0L
                var lastEmit = 0L
                body.byteStream().use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            downloaded += read
                            val now = System.currentTimeMillis()
                            if (now - lastEmit > 350L) {
                                lastEmit = now
                                onProgress(downloaded, total)
                            }
                        }
                        output.flush()
                    }
                }
                val finalSize = outFile.length()
                val completed = current.copy(
                    downloadedBytes = finalSize,
                    totalBytes = if (total > 0L) total else finalSize,
                    state = RealDownloadState.COMPLETED,
                    statusText = "مكتمل 100%",
                    localFilePath = outFile.absolutePath,
                    localUri = Uri.fromFile(outFile).toString()
                )
                updateRecordInPrefs(completed)
                completed
            }
        } catch (e: Exception) {
            runCatching { outFile.delete() }
            val failed = current.copy(
                state = RealDownloadState.FAILED,
                statusText = "فشل التنزيل: ${e.localizedMessage ?: "خطأ اتصال"}",
                errorDetail = e.localizedMessage
            )
            updateRecordInPrefs(failed)
            failed
        }
    }

    fun refreshFromDownloadManager(): List<RealDownloadRecord> {
        val current = loadRecords()
        val activeDmIds = current
            .filter {
                it.downloadManagerId > 0L &&
                    it.state != RealDownloadState.COMPLETED &&
                    it.state != RealDownloadState.FAILED
            }
            .map { it.downloadManagerId }
        if (activeDmIds.isEmpty()) return current

        val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return current
        val query = DownloadManager.Query().setFilterById(*activeDmIds.toLongArray())
        val updatedMap = mutableMapOf<Long, RealDownloadRecord>()
        val existingByDmId = current.associateBy { it.downloadManagerId }

        runCatching {
            dm.query(query)?.use { cursor ->
                val colId = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                val colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val colBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val colTotal = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val colLocalUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val colReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)

                while (cursor.moveToNext()) {
                    val dmId = if (colId >= 0) cursor.getLong(colId) else -1L
                    val orig = existingByDmId[dmId] ?: continue
                    val dmStatus = if (colStatus >= 0) cursor.getInt(colStatus) else DownloadManager.STATUS_PENDING
                    val bytes = if (colBytes >= 0) cursor.getLong(colBytes).coerceAtLeast(0L) else orig.downloadedBytes
                    val total = if (colTotal >= 0) cursor.getLong(colTotal).coerceAtLeast(0L) else orig.totalBytes
                    val localUri = if (colLocalUri >= 0) cursor.getString(colLocalUri) else orig.localUri
                    val reason = if (colReason >= 0) cursor.getInt(colReason) else 0

                    val (nextState, text) = when (dmStatus) {
                        DownloadManager.STATUS_SUCCESSFUL -> RealDownloadState.COMPLETED to "مكتمل على الجهاز"
                        DownloadManager.STATUS_FAILED -> RealDownloadState.FAILED to "فشل التنزيل (رمز $reason)"
                        DownloadManager.STATUS_PAUSED -> RealDownloadState.PAUSED to "متوقف مؤقتاً"
                        DownloadManager.STATUS_RUNNING -> {
                            val pct = if (total > 0L) "${((bytes * 100) / total).coerceIn(0, 100)}%" else "جاري التنزيل..."
                            RealDownloadState.RUNNING to "جاري التنزيل ($pct)"
                        }
                        else -> RealDownloadState.PENDING to "في قائمة الانتظار..."
                    }
                    updatedMap[dmId] = orig.copy(
                        downloadedBytes = bytes,
                        totalBytes = total,
                        state = nextState,
                        statusText = text,
                        localUri = localUri,
                        errorDetail = if (dmStatus == DownloadManager.STATUS_FAILED) "رمز الخطأ: $reason" else null
                    )
                }
            }
        }

        if (updatedMap.isEmpty()) return current
        val nextList = current.map { updatedMap[it.downloadManagerId] ?: it }
        saveRecords(nextList)
        return nextList
    }

    fun removeRecord(record: RealDownloadRecord): List<RealDownloadRecord> {
        if (record.downloadManagerId > 0L) {
            runCatching {
                val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                dm?.remove(record.downloadManagerId)
            }
        }
        record.localFilePath?.let { path -> runCatching { File(path).delete() } }
        val next = loadRecords().filterNot { it.id == record.id }
        saveRecords(next)
        return next
    }

    private fun updateRecordInPrefs(record: RealDownloadRecord) {
        val next = loadRecords().map { if (it.id == record.id) record else it }
        saveRecords(next)
    }

    companion object {
        private const val KEY_RECORDS = "real_downloads_v1"
    }
}
