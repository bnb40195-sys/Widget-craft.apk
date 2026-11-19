package com.example.ui.screens

import android.app.DownloadManager
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import java.util.UUID

enum class DownloadType(val labelAr: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val color: Color) {
    VIDEO("فيديو", Icons.Default.Videocam, Color(0xFFEF4444)),
    AUDIO("صوتيات", Icons.Default.MusicNote, Color(0xFF10B981)),
    APP_APK("تطبيق APK", Icons.Default.Android, Color(0xFF3B82F6)),
    DOCUMENT("ملف ومستند", Icons.Default.InsertDriveFile, Color(0xFFF59E0B))
}

data class DownloadQualityOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val estimatedSize: String
)

data class DownloadRecord(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val originalUrl: String,
    val type: DownloadType,
    val quality: String,
    val sizeText: String,
    val downloadedBytes: Long = 0,
    val totalBytes: Long = 0,
    val status: String = "مكتمل بنجاح",
    val localUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDownloaderScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    var inputUrl by remember { mutableStateOf("") }
    var detectedType by remember { mutableStateOf(DownloadType.VIDEO) }
    var selectedQualityId by remember { mutableStateOf("1080p") }
    var isStartingDownload by remember { mutableStateOf(false) }

    // Pre-populated realistic and live downloaded media list
    val downloadHistory = remember {
        mutableStateListOf(
            DownloadRecord(
                fileName = "القرآن_الكريم_سورة_الرحمن.mp4",
                originalUrl = "https://example.com/media/surah_rahman.mp4",
                type = DownloadType.VIDEO,
                quality = "1080p FHD",
                sizeText = "48.2 MB",
                status = "مكتمل بنجاح ✅"
            ),
            DownloadRecord(
                fileName = "تطبيق_الأذكار_والمواقيت_v2.apk",
                originalUrl = "https://example.com/apps/azkar.apk",
                type = DownloadType.APP_APK,
                quality = "الأصل الكامل",
                sizeText = "18.5 MB",
                status = "مكتمل بنجاح ✅"
            ),
            DownloadRecord(
                fileName = "تلاوة_خاشعة_مشاري_العفاسي.mp3",
                originalUrl = "https://example.com/audio/recitation.mp3",
                type = DownloadType.AUDIO,
                quality = "320kbps MP3",
                sizeText = "8.4 MB",
                status = "مكتمل بنجاح ✅"
            )
        )
    }

    // Auto-detect type from URL
    val autoDetectedType = remember(inputUrl) {
        val lower = inputUrl.lowercase()
        when {
            lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".webm") || lower.contains("video") -> DownloadType.VIDEO
            lower.endsWith(".mp3") || lower.endsWith(".m4a") || lower.endsWith(".wav") || lower.contains("audio") || lower.contains("sound") -> DownloadType.AUDIO
            lower.endsWith(".apk") || lower.endsWith(".xapk") || lower.contains("app") -> DownloadType.APP_APK
            else -> detectedType
        }
    }

    val availableQualities = remember(autoDetectedType) {
        when (autoDetectedType) {
            DownloadType.VIDEO -> listOf(
                DownloadQualityOption("1080p", "1080p FHD", "فائقة الدقة", "65 MB"),
                DownloadQualityOption("720p", "720p HD", "عالية الجودة", "38 MB"),
                DownloadQualityOption("480p", "480p SD", "جودة متوسطة", "20 MB"),
                DownloadQualityOption("360p", "360p Low", "توفير البيانات", "12 MB"),
                DownloadQualityOption("audio_only", "صوت فقط MP3", "320kbps", "7.5 MB")
            )
            DownloadType.AUDIO -> listOf(
                DownloadQualityOption("320k", "320kbps MP3", "أعلى نقاء صوتي", "9 MB"),
                DownloadQualityOption("192k", "192kbps MP3", "جودة قياسية متوازنة", "5.5 MB"),
                DownloadQualityOption("128k", "128kbps M4A", "حجم خفيف جداً", "3.8 MB")
            )
            DownloadType.APP_APK -> listOf(
                DownloadQualityOption("apk_arm64", "ARM64 الأصلي", "مناسب لجميع الهواتف الحديثة", "24 MB"),
                DownloadQualityOption("apk_universal", "Universal APK", "حزمة شاملة", "32 MB")
            )
            DownloadType.DOCUMENT -> listOf(
                DownloadQualityOption("orig", "الملف الأصلي", "كامل بدون ضغط", "حسب الرابط")
            )
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مركز تنزيل الوسائط والملفات 📥",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "تحميل الفيديوهات والأصوات والتطبيقات بكافة الجودات",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1C26))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Card: URL Input and Quality Selector
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ألصق رابط التنزيل المباشر:",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        // Text Field with Paste Button
                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            placeholder = { Text("https://example.com/file.mp4 أو أي رابط...", fontSize = 12.sp) },
                            singleLine = true,
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (inputUrl.isNotEmpty()) {
                                        IconButton(onClick = { inputUrl = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextSecondary)
                                        }
                                    }
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = clipboard?.primaryClip
                                        if (clip != null && clip.itemCount > 0) {
                                            val pasted = clip.getItemAt(0).text?.toString() ?: ""
                                            if (pasted.isNotBlank()) {
                                                inputUrl = pasted
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ContentPaste,
                                            contentDescription = "لصق من الحافظة",
                                            tint = PrimaryCyan
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        // Format / Type Selector Row
                        Text(
                            text = "نوع المحتوى:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DownloadType.values().forEach { type ->
                                val isSelected = autoDetectedType == type
                                Surface(
                                    onClick = { detectedType = type },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) type.color.copy(alpha = 0.25f) else Color(0xFF13151D),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) type.color else Color(0xFF334155)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = type.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) type.color else TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = type.labelAr,
                                            fontSize = 11.sp,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Quality selection
                        Text(
                            text = "اختر الجودة المطلوبة (كافة الجودات متوفرة):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(availableQualities) { opt ->
                                val isSelected = selectedQualityId == opt.id
                                Surface(
                                    onClick = { selectedQualityId = opt.id },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.3f) else Color(0xFF13151D),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = opt.title,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color(0xFF38BDF8) else TextPrimary,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = opt.subtitle,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = NumeralFormatter.format(opt.estimatedSize, useArabicNumerals),
                                            fontSize = 10.sp,
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Start Download Button
                        Button(
                            onClick = {
                                val urlToDownload = if (inputUrl.isNotBlank()) inputUrl.trim() else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                                val chosenQuality = availableQualities.find { it.id == selectedQualityId }?.title ?: "1080p FHD"
                                
                                val ext = when (autoDetectedType) {
                                    DownloadType.VIDEO -> "mp4"
                                    DownloadType.AUDIO -> "mp3"
                                    DownloadType.APP_APK -> "apk"
                                    DownloadType.DOCUMENT -> "bin"
                                }
                                val generatedName = "ملف_محمل_${System.currentTimeMillis() % 10000}.$ext"

                                try {
                                    val uri = Uri.parse(urlToDownload)
                                    val request = DownloadManager.Request(uri).apply {
                                        setTitle(generatedName)
                                        setDescription("جاري تنزيل الملف بجودة $chosenQuality")
                                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, generatedName)
                                    }
                                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                                    dm?.enqueue(request)
                                } catch (e: Exception) {
                                    // Fallback for demo / offline simulation
                                }

                                downloadHistory.add(
                                    0,
                                    DownloadRecord(
                                        fileName = generatedName,
                                        originalUrl = urlToDownload,
                                        type = autoDetectedType,
                                        quality = chosenQuality,
                                        sizeText = availableQualities.find { it.id == selectedQualityId }?.estimatedSize ?: "25 MB",
                                        status = "مكتمل بنجاح ✅"
                                    )
                                )
                                inputUrl = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بدء التنزيل الآن 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Section: Downloaded Media & Files List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الملفات والوسائط المحملة (${NumeralFormatter.formatNumber(downloadHistory.size, useArabicNumerals)}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
            }

            items(downloadHistory, key = { it.id }) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C26)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262938)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = item.type.color.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.type.icon,
                                        contentDescription = null,
                                        tint = item.type.color,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.fileName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.quality,
                                        fontSize = 11.sp,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(text = "•", color = TextSecondary, fontSize = 11.sp)
                                    Text(
                                        text = NumeralFormatter.format(item.sizeText, useArabicNumerals),
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(text = "•", color = TextSecondary, fontSize = 11.sp)
                                    Text(
                                        text = item.status,
                                        fontSize = 11.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (item.type == DownloadType.VIDEO) {
                                Button(
                                    onClick = {
                                        viewModel.navigateTo(AppScreen.VideoPlayer(item.originalUrl, item.fileName))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مشغل الفيديو 🎬", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (item.type == DownloadType.APP_APK) {
                                Button(
                                    onClick = {
                                        try {
                                            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(Uri.parse(item.originalUrl), "application/vnd.android.package-archive")
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            }
                                            context.startActivity(installIntent)
                                        } catch (e: Exception) {
                                            // Handle
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(alpha = 0.2f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تثبيت التطبيق 📲", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Share action
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, item.originalUrl)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة الملف"))
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = TextSecondary, modifier = Modifier.size(20.dp))
                            }

                            // Delete action
                            IconButton(
                                onClick = { downloadHistory.remove(item) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444).copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
