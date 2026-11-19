package com.example.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WidgetAlert
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AlertSoundPreset(
    val id: String,
    val title: String,
    val iconDesc: String,
    val resId: Int?,
    val customUri: String? = null
)

val availableAlertSounds = listOf(
    AlertSoundPreset("alert_sound", "رنين التنبيه التفاعلي", "🔔", R.raw.alert_sound),
    AlertSoundPreset("adhan_chime", "أذان الحرم المكي الشريف", "🕌", R.raw.adhan_chime),
    AlertSoundPreset("quran_recitation", "تلاوة قرآنية هادئة", "📖", R.raw.quran_recitation),
    AlertSoundPreset("silent", "صامت (اهتزاز فقط)", "🔕", null)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val alerts by viewModel.alerts.collectAsState()
    val is24HourFormat by viewModel.is24HourFormat.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تنبيهات وتذكيرات الويدجات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "منبهات وتذكيرات مهام مخصصة",
                            fontSize = 11.sp,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_alerts")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    OfflineStatusBadge(modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("btn_add_alert")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DarkBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة تنبيه جديد", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 12-Hour vs 24-Hour Format Switch Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (is24HourFormat) "نظام 24 ساعة (00:00 - 23:59)" else "نظام 12 ساعة (ص / م)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (is24HourFormat) "معطل ص/م - التوقيت 24 ساعة" else "مفعل ص/م - التوقيت 12 ساعة",
                                    fontSize = 11.sp,
                                    color = if (is24HourFormat) PrimaryCyan else TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = is24HourFormat,
                            onCheckedChange = { viewModel.toggle24HourFormat(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBackground,
                                checkedTrackColor = PrimaryCyan
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "تنبيهات مع أصوات اختيارية والاهتزاز",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "يمكنك كتابة التوقيت يدوياً واختيار صوت الأذان أو التنبيه وتعمل التنبيهات حتى عند إغلاق التطبيق.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(alerts, key = { it.id }) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(
                        1.dp,
                        if (alert.isEnabled) PrimaryCyan.copy(alpha = 0.4f) else DarkBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = alert.formatTime(is24HourFormat),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = if (alert.isEnabled) PrimaryCyan else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = alert.repeatMode,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = alert.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (alert.soundId == "silent") Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = alert.soundTitle,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.testTriggerAlert(alert) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Test Alert",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteAlert(alert.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Switch(
                                checked = alert.isEnabled,
                                onCheckedChange = { viewModel.toggleAlert(alert.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBackground,
                                    checkedTrackColor = PrimaryCyan
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Alert Dialog with Manual Typing & Sound Selector
    if (showAddDialog) {
        AddAlertDialog(
            is24Hour = is24HourFormat,
            onDismiss = { showAddDialog = false },
            onSave = { alert ->
                viewModel.addOrUpdateAlert(alert)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddAlertDialog(
    is24Hour: Boolean,
    onDismiss: () -> Unit,
    onSave: (WidgetAlert) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("تذكير جديد") }
    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(30) }
    var manualTimeText by remember { mutableStateOf("08:30") }
    var repeatMode by remember { mutableStateOf("يومياً") }
    var vibrate by remember { mutableStateOf(true) }
    var selectedSound by remember { mutableStateOf(availableAlertSounds[0]) }

    var previewPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPreviewing by remember { mutableStateOf(false) }

    fun stopPreview() {
        previewPlayer?.stop()
        previewPlayer?.release()
        previewPlayer = null
        isPreviewing = false
    }

    fun playPreview(sound: AlertSoundPreset) {
        stopPreview()
        try {
            val mp = if (sound.customUri != null) {
                MediaPlayer.create(context, Uri.parse(sound.customUri))
            } else if (sound.resId != null) {
                MediaPlayer.create(context, sound.resId)
            } else null

            if (mp != null) {
                previewPlayer = mp
                mp.setOnCompletionListener {
                    isPreviewing = false
                    stopPreview()
                }
                mp.start()
                isPreviewing = true
            }
        } catch (e: Exception) {
            isPreviewing = false
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "صوت من الهاتف"
            val customPreset = AlertSoundPreset(
                id = "custom_phone_audio",
                title = "صوت مخصص: $fileName",
                iconDesc = "📱",
                resId = null,
                customUri = uri.toString()
            )
            selectedSound = customPreset
            playPreview(customPreset)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer?.release()
            previewPlayer = null
        }
    }

    fun updateFromManualInput(input: String) {
        manualTimeText = input
        val clean = input.trim()
        val isPm = clean.contains("م") || clean.lowercase().contains("pm")
        val isAm = clean.contains("ص") || clean.lowercase().contains("am")
        val digits = clean.replace("[^0-9:]".toRegex(), "")

        if (digits.contains(":")) {
            val parts = digits.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull()
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            if (h != null) {
                var finalHour = h
                if (isPm && finalHour < 12) finalHour += 12
                if (isAm && finalHour == 12) finalHour = 0
                hour = finalHour.coerceIn(0, 23)
                minute = m.coerceIn(0, 59)
            }
        } else if (digits.length in 3..4) {
            val h = digits.substring(0, digits.length - 2).toIntOrNull()
            val m = digits.substring(digits.length - 2).toIntOrNull() ?: 0
            if (h != null) {
                hour = h.coerceIn(0, 23)
                minute = m.coerceIn(0, 59)
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            stopPreview()
            onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Alarm, contentDescription = null, tint = PrimaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إضافة تنبيه وتوقيت مخصص", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان التنبيه") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Manual Time Input by Typing
                item {
                    Text(
                        text = "✍️ كتابة التوقيت يدوياً (اختياري بكتابة):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                    OutlinedTextField(
                        value = manualTimeText,
                        onValueChange = { updateFromManualInput(it) },
                        label = { Text("اكتب الوقت (مثال: 14:30 أو 08:15 أو 2:30 م)") },
                        placeholder = { Text("14:30 أو 08:30 ص") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Numeric Hour & Minute Pickers with Live Formatted Preview
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("معاينة الوقت المحدد:", fontSize = 11.sp, color = TextSecondary)
                                Surface(
                                    color = PrimaryCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    val formatted = if (is24Hour) {
                                        String.format(java.util.Locale.US, "%02d:%02d (24س)", hour, minute)
                                    } else {
                                        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                                        val p = if (hour < 12) "ص" else "م"
                                        String.format(java.util.Locale.US, "%02d:%02d %s (12س)", h, minute, p)
                                    }
                                    Text(
                                        text = formatted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = hour.toString(),
                                    onValueChange = {
                                        val newH = (it.toIntOrNull() ?: 0).coerceIn(0, 23)
                                        hour = newH
                                        manualTimeText = String.format(java.util.Locale.US, "%02d:%02d", newH, minute)
                                    },
                                    label = { Text("الساعة (0-23)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = minute.toString(),
                                    onValueChange = {
                                        val newM = (it.toIntOrNull() ?: 0).coerceIn(0, 59)
                                        minute = newM
                                        manualTimeText = String.format(java.util.Locale.US, "%02d:%02d", hour, newM)
                                    },
                                    label = { Text("الدقيقة (0-59)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Quick Times 1-Tap Chips
                item {
                    Text("أوقات سريعة بنقرة واحدة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("الفجر 05:00", 5, 0),
                            Triple("الظهر 12:30", 12, 30),
                            Triple("المغرب 18:15", 18, 15),
                            Triple("العشاء 20:00", 20, 0)
                        ).forEach { (label, h, m) ->
                            FilterChip(
                                selected = hour == h && minute == m,
                                onClick = {
                                    hour = h
                                    minute = m
                                    manualTimeText = String.format(java.util.Locale.US, "%02d:%02d", h, m)
                                },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryCyan.copy(alpha = 0.25f)
                                )
                            )
                        }
                    }
                }

                // Optional Sound Selector
                item {
                    Text(
                        text = "🔊 صوت التنبيه الاختياري:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableAlertSounds.forEach { sound ->
                            val isSelected = selectedSound.id == sound.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) PrimaryCyan else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSound = sound
                                        playPreview(sound)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(sound.iconDesc, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = sound.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PrimaryCyan else TextPrimary
                                        )
                                    }

                                    if (sound.resId != null) {
                                        IconButton(
                                            onClick = {
                                                if (isPreviewing && selectedSound.id == sound.id) {
                                                    stopPreview()
                                                } else {
                                                    selectedSound = sound
                                                    playPreview(sound)
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isPreviewing && selectedSound.id == sound.id) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = "Preview Sound",
                                                tint = if (isSelected) PrimaryCyan else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Upload Audio from Phone button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { audioPickerLauncher.launch("audio/*") }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("📁 رفع صوت مخصص من ذاكرة الهاتف (MP3/WAV)", color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Repeat Mode
                item {
                    Text("التكرار:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("يومياً", "مرة واحدة", "أيام العمل").forEach { rep ->
                            FilterChip(
                                selected = repeatMode == rep,
                                onClick = { repeatMode = rep },
                                label = { Text(rep, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryCyan.copy(alpha = 0.25f)
                                )
                            )
                        }
                    }
                }

                // Vibration Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الاهتزاز عند التنبيه", color = TextPrimary, fontSize = 13.sp)
                        Switch(
                            checked = vibrate,
                            onCheckedChange = { vibrate = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBackground,
                                checkedTrackColor = PrimaryCyan
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    stopPreview()
                    val alert = WidgetAlert(
                        title = title,
                        hour = hour,
                        minute = minute,
                        repeatMode = repeatMode,
                        vibrateEnabled = vibrate,
                        soundEnabled = selectedSound.id != "silent",
                        soundId = selectedSound.id,
                        soundTitle = "${selectedSound.iconDesc} ${selectedSound.title}",
                        customAudioUri = selectedSound.customUri
                    )
                    onSave(alert)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text("حفظ التنبيه", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                stopPreview()
                onDismiss()
            }) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}
