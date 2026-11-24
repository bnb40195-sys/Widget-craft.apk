package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.downloads.VideoQualityCatalog
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
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
            if (!initialVideoUri.isNullOrBlank()) Uri.parse(initialVideoUri) else null
        )
    }
    var urlInputText by remember { mutableStateOf(initialVideoUri.orEmpty()) }
    var currentVideoTitle by remember {
        mutableStateOf(initialVideoTitle ?: "مشغل الفيديوهات")
    }

    val qualityProfiles = VideoQualityCatalog.allVideoQualities
    var selectedQualityId by remember { mutableStateOf("auto") }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isFillScreen by remember { mutableStateOf(false) }
    var detectedResolution by remember { mutableStateOf("تلقائي") }
    var playerErrorMessage by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    fun applyQualityConstraint(qualityId: String) {
        selectedQualityId = qualityId
        val spec = qualityProfiles.firstOrNull { it.id == qualityId }
        val builder = exoPlayer.trackSelectionParameters.buildUpon()
        if (spec == null || spec.id == "auto") {
            builder.clearVideoSizeConstraints()
        } else {
            builder.setMaxVideoSize(spec.maxWidth, spec.maxHeight)
        }
        exoPlayer.trackSelectionParameters = builder.build()
    }

    fun loadAndPlayUri(uri: Uri, title: String) {
        playerErrorMessage = null
        currentVideoUri = uri
        currentVideoTitle = title
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun playFromUrlInput() {
        val cleaned = urlInputText.trim()
        if (!cleaned.startsWith("http://", ignoreCase = true) &&
            !cleaned.startsWith("https://", ignoreCase = true)
        ) {
            viewModel.showToast("أدخل رابط فيديو صالح يبدأ بـ http:// أو https://")
            return
        }
        val uri = runCatching { Uri.parse(cleaned) }.getOrNull()
        if (uri == null) {
            viewModel.showToast("الرابط غير صالح")
            return
        }
        val title = uri.lastPathSegment?.takeIf { it.isNotBlank() } ?: "بث فيديو عبر الرابط"
        loadAndPlayUri(uri, title)
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    playerErrorMessage = null
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    detectedResolution = "${videoSize.width}×${videoSize.height}"
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isPlaying = false
                isBuffering = false
                playerErrorMessage = "تعذر تشغيل الفيديو أو فك تشفيره (${error.errorCodeName}). تأكد من الرابط المباشر أو جرّب ملفاً محلياً."
            }
        }
        exoPlayer.addListener(listener)
        if (currentVideoUri != null) {
            loadAndPlayUri(currentVideoUri!!, currentVideoTitle)
        }
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying, currentVideoUri) {
        while (currentVideoUri != null) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            val d = exoPlayer.duration
            if (d > 0L) durationMs = d
            delay(400)
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            loadAndPlayUri(uri, "فيديو من معرض الجهاز")
        }
    }

    val fileManagerVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            loadAndPlayUri(uri, "فيديو من مدير الملفات")
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مشغل الفيديوهات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = currentVideoTitle,
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        fileManagerVideoLauncher.launch(arrayOf("video/*"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "فتح من مدير الملفات",
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
            // Video Display Viewport (Media3 PlayerView)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isFillScreen) 1.25f else 1.77f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (currentVideoUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        update = { view ->
                            view.resizeMode = if (isFillScreen) {
                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            } else {
                                AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isBuffering && playerErrorMessage == null) {
                        CircularProgressIndicator(color = PrimaryCyan, modifier = Modifier.size(36.dp))
                    }

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
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(34.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = playerErrorMessage!!,
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            currentVideoUri?.let { loadAndPlayUri(it, currentVideoTitle) }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إعادة المحاولة", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { fileManagerVideoLauncher.launch(arrayOf("video/*")) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("من مدير الملفات", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E202B),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "تشغيل الفيديو من الرابط أو الجهاز",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "يدعم كافة الجودات من 144p إلى 12K وروابط MP4 / HLS المباشرة",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { fileManagerVideoLauncher.launch(arrayOf("video/*")) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مدير الملفات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("المعرض", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quality Selector Row: 144p all the way up to 12K
            Surface(
                color = Color(0xFF1A1C26),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("الجودة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(qualityProfiles) { q ->
                            val isSelected = selectedQualityId == q.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    applyQualityConstraint(q.id)
                                    viewModel.showToast("تم ضبط جودة التشغيل: ${q.title}")
                                },
                                label = {
                                    Text(
                                        text = q.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2563EB).copy(alpha = 0.35f),
                                    selectedLabelColor = Color(0xFF38BDF8)
                                ),
                                modifier = Modifier.testTag("video_quality_${q.id}")
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
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    val progressFloat = if (durationMs > 0L) {
                        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Slider(
                        value = progressFloat,
                        onValueChange = { frac ->
                            val seekTo = (frac * durationMs).toLong()
                            currentPositionMs = seekTo
                            exoPlayer.seekTo(seekTo)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF2563EB),
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatDuration(currentPositionMs, useArabicNumerals), color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = formatDuration(durationMs, useArabicNumerals), color = TextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                    exoPlayer.setPlaybackSpeed(playbackSpeed)
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

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val newPos = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                exoPlayer.seekTo(newPos)
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
                                        if (exoPlayer.isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            if (currentVideoUri != null) {
                                                exoPlayer.play()
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
                                val target = if (durationMs > 0L) {
                                    (exoPlayer.currentPosition + 10_000L).coerceAtMost(durationMs)
                                } else exoPlayer.currentPosition + 10_000L
                                exoPlayer.seekTo(target)
                                currentPositionMs = target
                            }) {
                                Icon(Icons.Default.FastForward, contentDescription = "+10 ثوان", tint = Color.White)
                            }
                        }

                        IconButton(onClick = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        }) {
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

            Spacer(modifier = Modifier.height(14.dp))

            // Play via Direct URL Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تشغيل فيديو عن طريق رابط مباشر (URL)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }

                    OutlinedTextField(
                        value = urlInputText,
                        onValueChange = { urlInputText = it },
                        placeholder = { Text("https://example.com/video.mp4 أو .m3u8", fontSize = 12.sp) },
                        singleLine = true,
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (urlInputText.isNotEmpty()) {
                                    IconButton(onClick = { urlInputText = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextSecondary)
                                    }
                                }
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val pasted = clipboard?.primaryClip?.takeIf { it.itemCount > 0 }
                                        ?.getItemAt(0)?.text?.toString().orEmpty()
                                    if (pasted.isNotBlank()) urlInputText = pasted
                                }) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = "لصق", tint = PrimaryCyan)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_url_input")
                    )

                    Button(
                        onClick = { playFromUrlInput() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("video_play_url_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تشغيل الرابط الآن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Video Specs Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مواصفات البث والجودة:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الجودة المحددة:", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = qualityProfiles.find { it.id == selectedQualityId }?.title ?: "Auto",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الدقة الفعلية:", color = TextSecondary, fontSize = 11.sp)
                        Text(detectedResolution, color = TextPrimary, fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الحد المدعوم:", color = TextSecondary, fontSize = 11.sp)
                        Text("144p إلى 12K (6480p)", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatDuration(ms: Long, useArabic: Boolean): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    val formatted = if (hours > 0L) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    return NumeralFormatter.format(formatted, useArabic)
}
