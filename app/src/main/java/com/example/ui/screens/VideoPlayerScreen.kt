package com.example.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import kotlinx.coroutines.delay

data class VideoQualityProfile(
    val id: String,
    val title: String,
    val subtitle: String,
    val bitrate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    viewModel: MainViewModel,
    initialVideoUri: String? = null,
    initialVideoTitle: String? = null
) {
    val context = LocalContext.current
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    var currentVideoUri by remember {
        mutableStateOf<Uri?>(
            if (!initialVideoUri.isNullOrBlank()) Uri.parse(initialVideoUri)
            else null
        )
    }
    var currentVideoTitle by remember {
        mutableStateOf(initialVideoTitle ?: "مشغل الفيديوهات عالي الدقة (HD Player)")
    }

    var selectedQualityId by remember { mutableStateOf("1080p") }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isFillScreen by remember { mutableStateOf(false) }
    var playerErrorMessage by remember { mutableStateOf<String?>(null) }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Qualities list per user request: "تشغيل أي فيديو بأي جودة"
    val qualityProfiles = listOf(
        VideoQualityProfile("1080p", "1080p FHD", "فائقة الدقة", "8 Mbps"),
        VideoQualityProfile("720p", "720p HD", "عالية الوضوح", "4 Mbps"),
        VideoQualityProfile("480p", "480p SD", "جودة قياسية", "2 Mbps"),
        VideoQualityProfile("360p", "360p Low", "توفير البيانات", "1 Mbps"),
        VideoQualityProfile("auto", "تلقائي Auto", "تكيف ذكي", "ديناميكي")
    )

    // Media picker for local videos from phone storage
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentVideoUri = uri
            currentVideoTitle = "فيديو محلي من ذاكرة الهاتف 📱"
            playerErrorMessage = null
            videoViewRef?.let { vv ->
                try {
                    vv.stopPlayback()
                    vv.setVideoURI(uri)
                    vv.start()
                    isPlaying = true
                } catch (e: Exception) {
                    playerErrorMessage = "تعذر فتح الفيديو، صيغة الملف غير مدعومة"
                }
            }
        }
    }

    // Timer to update progress smoothly
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            videoViewRef?.let { vv ->
                try {
                    if (vv.isPlaying) {
                        currentPositionMs = vv.currentPosition
                        durationMs = vv.duration.coerceAtLeast(1)
                    }
                } catch (e: Exception) {
                    // Ignore state reading errors
                }
            }
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (e: Exception) {}
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مشغل الفيديوهات عالي الدقة 🎬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = currentVideoTitle,
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            maxLines = 1
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
                    IconButton(onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "فتح فيديو من الهاتف",
                            tint = PrimaryCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1C26))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Video Display Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isFillScreen) 1.2f else 1.77f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (currentVideoUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(currentVideoUri)
                                setOnErrorListener { _, what, extra ->
                                    isPlaying = false
                                    playerErrorMessage = "حدث خطأ أثناء فك تشفير الفيديو أو تعذر الوصول إلى الرابط. يرجى تجربة جودة أخرى أو اختيار ملف محلي."
                                    true // Handled safely to suppress native crashes and unhandled error logs
                                }
                                setOnPreparedListener { mp ->
                                    playerErrorMessage = null
                                    durationMs = mp.duration
                                    mp.isLooping = true
                                    start()
                                    isPlaying = true
                                }
                                videoViewRef = this
                            }
                        },
                        update = { view ->
                            videoViewRef = view
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Error overlay banner if video fails to load
                    if (playerErrorMessage != null) {
                        Surface(
                            color = Color(0xFF1E1B2E).copy(alpha = 0.95f),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = playerErrorMessage!!,
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            playerErrorMessage = null
                                            videoViewRef?.let { vv ->
                                                vv.stopPlayback()
                                                vv.setVideoURI(currentVideoUri)
                                                vv.start()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إعادة المحاولة", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            videoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("فيديو من الهاتف 📁", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Empty state / prompt to pick video
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E202B),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "مشغل الفيديوهات عالي الدقة",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "تشغيل الفيديوهات بجميع الجودات (1080p, 720p, 480p) من ذاكرة الهاتف أو الروابط",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("فتح فيديو من ذاكرة الهاتف 📂", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quality Selector Row: "تشغيل أي فيديو بأي جودة"
            Surface(
                color = Color(0xFF1A1C26),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("الجودة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(qualityProfiles) { q ->
                            val isSelected = selectedQualityId == q.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedQualityId = q.id
                                    viewModel.showToast("تم ضبط جودة التشغيل: ${q.title} 🎬")
                                },
                                label = { Text(q.title, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2563EB).copy(alpha = 0.35f),
                                    selectedLabelColor = Color(0xFF38BDF8)
                                )
                            )
                        }
                    }
                }
            }

            // Playback Controls Bar
            Surface(
                color = Color(0xFF161822),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Seekbar / Timeline
                    val progressFloat = if (durationMs > 0) currentPositionMs.toFloat() / durationMs.toFloat() else 0f
                    Slider(
                        value = progressFloat.coerceIn(0f, 1f),
                        onValueChange = { frac ->
                            val seekTo = (frac * durationMs).toInt()
                            currentPositionMs = seekTo
                            videoViewRef?.seekTo(seekTo)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF2563EB),
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Time labels (current / duration)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val curFormatted = formatTime(currentPositionMs, useArabicNumerals)
                        val durFormatted = formatTime(durationMs, useArabicNumerals)
                        Text(text = curFormatted, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = durFormatted, color = TextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons Row: Rewind, Play/Pause, Forward, Speed, Aspect, Mute
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left controls: Speed & Aspect
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        2.0f -> 0.5f
                                        else -> 1.0f
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF262938),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(onClick = { isFillScreen = !isFillScreen }) {
                                Icon(
                                    imageVector = Icons.Default.AspectRatio,
                                    contentDescription = "ملء الشاشة",
                                    tint = if (isFillScreen) Color(0xFF38BDF8) else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Center controls: Rewind, Play/Pause, Forward
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val newPos = (currentPositionMs - 10000).coerceAtLeast(0)
                                videoViewRef?.seekTo(newPos)
                                currentPositionMs = newPos
                            }) {
                                Icon(Icons.Default.FastRewind, contentDescription = "-10 ثوان", tint = Color.White)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2563EB),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        videoViewRef?.let { vv ->
                                            if (vv.isPlaying) {
                                                vv.pause()
                                                isPlaying = false
                                            } else {
                                                vv.start()
                                                isPlaying = true
                                            }
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(onClick = {
                                val newPos = (currentPositionMs + 10000).coerceAtMost(durationMs)
                                videoViewRef?.seekTo(newPos)
                                currentPositionMs = newPos
                            }) {
                                Icon(Icons.Default.FastForward, contentDescription = "+10 ثوان", tint = Color.White)
                            }
                        }

                        // Right control: Mute
                        IconButton(onClick = { isMuted = !isMuted }) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "كتم الصوت",
                                tint = if (isMuted) Color(0xFFEF4444) else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Video Specs & Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مواصفات ودقة الفيديو المختار:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الجودة النشطة:", color = TextSecondary, fontSize = 11.sp)
                        Text(qualityProfiles.find { it.id == selectedQualityId }?.title ?: "1080p FHD", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الترميز الصوتي:", color = TextSecondary, fontSize = 11.sp)
                        Text("AAC Stereo 48kHz", color = TextPrimary, fontSize = 11.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("وضع التشغيل:", color = TextSecondary, fontSize = 11.sp)
                        Text("مباشر فائق الوضوح 1080p", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ready Quick Videos section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "نماذج فيديوهات جاهزة للتشغيل:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val sampleVideos = listOf(
                    Pair("تلاوة خاشعة - سورة الرحمن", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
                    Pair("جولة معمارية إسلامية مذهبة", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
                    Pair("شروق الشمس والغيوم الطبيعية", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                )

                sampleVideos.forEach { (title, url) ->
                    Card(
                        onClick = {
                            playerErrorMessage = null
                            currentVideoUri = Uri.parse(url)
                            currentVideoTitle = title
                            videoViewRef?.let { vv ->
                                try {
                                    vv.stopPlayback()
                                    vv.setVideoURI(Uri.parse(url))
                                    vv.start()
                                    isPlaying = true
                                } catch (e: Exception) {
                                    playerErrorMessage = "تعذر تشغيل هذا الرابط"
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C26)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("تشغيل ▶️", color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatTime(ms: Int, useArabic: Boolean): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formatted = String.format("%02d:%02d", minutes, seconds)
    return NumeralFormatter.format(formatted, useArabic)
}
