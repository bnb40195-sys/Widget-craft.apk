package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.Formatter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.downloads.MediaDownloadCategory
import com.example.data.downloads.RealDownloadRecord
import com.example.data.downloads.RealDownloadState
import com.example.data.downloads.RealDownloadStore
import com.example.data.downloads.VideoQualityCatalog
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun categoryIcon(cat: MediaDownloadCategory): ImageVector = when (cat) {
    MediaDownloadCategory.VIDEO -> Icons.Default.Videocam
    MediaDownloadCategory.AUDIO -> Icons.Default.MusicNote
    MediaDownloadCategory.APP_APK -> Icons.Default.Android
    MediaDownloadCategory.DOCUMENT -> Icons.Default.InsertDriveFile
}

private fun categoryColor(cat: MediaDownloadCategory): Color = when (cat) {
    MediaDownloadCategory.VIDEO -> Color(0xFFEF4444)
    MediaDownloadCategory.AUDIO -> Color(0xFF10B981)
    MediaDownloadCategory.APP_APK -> Color(0xFF3B82F6)
    MediaDownloadCategory.DOCUMENT -> Color(0xFFF59E0B)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDownloaderScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()
    val downloadStore = remember { RealDownloadStore(context) }

    BackHandler {
        viewModel.navigateBack()
    }

    var inputUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MediaDownloadCategory.VIDEO) }
    var selectedQualityId by remember { mutableStateOf("1080p") }
    var records by remember { mutableStateOf(downloadStore.loadRecords()) }

    val autoCategory = remember(inputUrl, selectedCategory) {
        val lower = inputUrl.lowercase()
        when {
            lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".webm") || lower.endsWith(".m3u8") -> MediaDownloadCategory.VIDEO
            lower.endsWith(".mp3") || lower.endsWith(".m4a") || lower.endsWith(".wav") -> MediaDownloadCategory.AUDIO
            lower.endsWith(".apk") || lower.endsWith(".xapk") -> MediaDownloadCategory.APP_APK
            else -> selectedCategory
        }
    }

    val availableQualities = remember(autoCategory) {
        when (autoCategory) {
            MediaDownloadCategory.VIDEO -> VideoQualityCatalog.allVideoQualities
            MediaDownloadCategory.AUDIO -> VideoQualityCatalog.audioQualities
            MediaDownloadCategory.APP_APK -> VideoQualityCatalog.apkQualities
            MediaDownloadCategory.DOCUMENT -> VideoQualityCatalog.documentQualities
        }
    }

    LaunchedEffect(availableQualities) {
        if (availableQualities.none { it.id == selectedQualityId }) {
            selectedQualityId = availableQualities.first().id
        }
    }

    // Real polling loop querying Android DownloadManager for active jobs
    LaunchedEffect(records) {
        val hasActiveDmJobs = records.any {
            it.downloadManagerId > 0L &&
                it.state != RealDownloadState.COMPLETED &&
                it.state != RealDownloadState.FAILED
        }
        if (hasActiveDmJobs) {
            delay(500)
            records = downloadStore.refreshFromDownloadManager()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مركز التنزيلات الحقيقي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "تنزيل فعلي عبر النظام بجودات من 144p إلى 12K",
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
                actions = {
                    IconButton(onClick = { records = downloadStore.refreshFromDownloadManager() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث الحالة", tint = PrimaryCyan)
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
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("أدخل رابط التنزيل المباشر:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        }

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            placeholder = { Text("https://example.com/video.mp4", fontSize = 12.sp) },
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
                                        val pasted = clipboard?.primaryClip?.takeIf { it.itemCount > 0 }
                                            ?.getItemAt(0)?.text?.toString().orEmpty()
                                        if (pasted.isNotBlank()) inputUrl = pasted
                                    }) {
                                        Icon(Icons.Default.ContentPaste, contentDescription = "لصق من الحافظة", tint = PrimaryCyan)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("downloader_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Text("نوع الملف:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MediaDownloadCategory.entries.forEach { cat ->
                                val isSelected = autoCategory == cat
                                val c = categoryColor(cat)
                                Surface(
                                    onClick = { selectedCategory = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) c.copy(alpha = 0.25f) else Color(0xFF13151D),
                                    border = BorderStroke(1.dp, if (isSelected) c else Color(0xFF334155)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(categoryIcon(cat), contentDescription = null, tint = if (isSelected) c else TextSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = cat.labelAr,
                                            fontSize = 10.sp,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Text("الجودة المستهدفة (144p إلى 12K):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
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
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = opt.title,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color(0xFF38BDF8) else TextPrimary,
                                            fontSize = 12.sp
                                        )
                                        Text(opt.subtitle, fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val chosenQuality = availableQualities.firstOrNull { it.id == selectedQualityId }?.title ?: "1080p FHD"
                                val startResult = downloadStore.startRealDownload(
                                    url = inputUrl,
                                    category = autoCategory,
                                    qualityLabel = chosenQuality
                                )
                                startResult
                                    .onSuccess { newRecord ->
                                        records = downloadStore.loadRecords()
                                        inputUrl = ""
                                        viewModel.showToast("بدأ التنزيل الحقيقي: ${newRecord.fileName}")
                                        if (newRecord.downloadManagerId <= 0L) {
                                            // Real OkHttp direct streaming fallback
                                            scope.launch {
                                                downloadStore.streamDirectHttpDownload(
                                                    recordId = newRecord.id,
                                                    url = newRecord.originalUrl,
                                                    fileName = newRecord.fileName
                                                ) { downloaded, total ->
                                                    records = records.map { r ->
                                                        if (r.id == newRecord.id) {
                                                            val pct = if (total > 0L) "${((downloaded * 100) / total).coerceIn(0, 100)}%" else ""
                                                            r.copy(
                                                                downloadedBytes = downloaded,
                                                                totalBytes = total,
                                                                statusText = "جاري التنزيل المباشر $pct"
                                                            )
                                                        } else r
                                                    }
                                                }
                                                records = downloadStore.loadRecords()
                                            }
                                        }
                                    }
                                    .onFailure { err ->
                                        viewModel.showToast(err.message ?: "رابط غير صالح")
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("downloader_start_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("بدء التنزيل الحقيقي", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "التنزيلات الحقيقية (${NumeralFormatter.formatNumber(records.size, useArabicNumerals)}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }

            if (records.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C26)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF262938)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(34.dp))
                            Text("لا توجد تنزيلات بعد", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "ألصق أي رابط مباشر أعلاه لتنزيل الملف فعلياً إلى مجلد التنزيلات في جهازك ومتابعة التقدم لحظياً.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(records, key = { it.id }) { item ->
                    RealDownloadRecordCard(
                        context = context,
                        item = item,
                        useArabicNumerals = useArabicNumerals,
                        onPlayVideo = {
                            val playableUri = item.localUri ?: item.originalUrl
                            viewModel.navigateTo(AppScreen.VideoPlayer(playableUri, item.fileName))
                        },
                        onDelete = {
                            records = downloadStore.removeRecord(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RealDownloadRecordCard(
    context: Context,
    item: RealDownloadRecord,
    useArabicNumerals: Boolean,
    onPlayVideo: () -> Unit,
    onDelete: () -> Unit
) {
    val c = categoryColor(item.category)
    val stateColor = when (item.state) {
        RealDownloadState.COMPLETED -> Color(0xFF10B981)
        RealDownloadState.FAILED -> Color(0xFFEF4444)
        else -> Color(0xFF38BDF8)
    }
    val sizeInfo = buildString {
        if (item.downloadedBytes > 0L) {
            append(Formatter.formatShortFileSize(context, item.downloadedBytes))
            if (item.totalBytes > 0L) {
                append(" / ")
                append(Formatter.formatShortFileSize(context, item.totalBytes))
            }
        } else if (item.totalBytes > 0L) {
            append(Formatter.formatShortFileSize(context, item.totalBytes))
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C26)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF262938)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = c.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(categoryIcon(item.category), contentDescription = null, tint = c, modifier = Modifier.size(22.dp))
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
                    Text(
                        text = listOfNotNull(
                            item.quality,
                            sizeInfo.takeIf { it.isNotBlank() }?.let { NumeralFormatter.format(it, useArabicNumerals) },
                            item.statusText
                        ).joinToString(" • "),
                        fontSize = 11.sp,
                        color = stateColor
                    )
                }
            }

            if (item.state == RealDownloadState.RUNNING || item.state == RealDownloadState.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                if (item.totalBytes > 0L) {
                    LinearProgressIndicator(
                        progress = { item.progressFraction },
                        color = PrimaryCyan,
                        trackColor = Color(0xFF2A3142),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(
                        color = PrimaryCyan,
                        trackColor = Color(0xFF2A3142),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.category == MediaDownloadCategory.VIDEO) {
                    Button(
                        onClick = onPlayVideo,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تشغيل في المشغل", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (item.category == MediaDownloadCategory.APP_APK && !item.localUri.isNullOrBlank()) {
                    Button(
                        onClick = {
                            runCatching {
                                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(item.localUri), "application/vnd.android.package-archive")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(installIntent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فتح الملف", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, item.originalUrl)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الرابط"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444).copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
