package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.filled.PushPin
import com.example.ui.theme.TextSecondary

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Apps

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShortcutsStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val shortcuts by viewModel.shortcuts.collectAsState()
    val notifShortcutsEnabled by viewModel.notificationShortcutsEnabled.collectAsState()
    val pinnedNotificationShortcuts by viewModel.pinnedNotificationShortcuts.collectAsState()

    var editingShortcut by remember { mutableStateOf<AppShortcut?>(null) }
    var isNewDialog by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleNotificationShortcuts(true)
        } else {
            viewModel.showToast("يلزم إذن الإشعارات لتفعيل شريط الإختصارات")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "إختصارات التطبيقات والمزدوجة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "تغيير شكل الأيقونات وإضافة تطبيقات مزدوجة وإخفاءها",
                            fontSize = 11.sp,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_shortcuts")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
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
                onClick = {
                    editingShortcut = AppShortcut(
                        name = "تطبيق جديد",
                        customLabel = "اختصار جديد",
                        iconColor = 0xFF38BDF8
                    )
                    isNewDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("btn_add_shortcut")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DarkBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة اختصار جديد", color = DarkBackground, fontWeight = FontWeight.Bold)
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
            // Notification Center Quick Shortcuts Management Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إختصارات مركز الإشعارات ⚡",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (notifShortcutsEnabled) "نشط (${pinnedNotificationShortcuts.size} مثبتة)" else "متوقف (اضغط للتفعيل)",
                                    fontSize = 11.sp,
                                    color = if (notifShortcutsEnabled) Color(0xFF34D399) else TextSecondary
                                )
                            }

                            Switch(
                                checked = notifShortcutsEnabled,
                                onCheckedChange = { enable ->
                                    if (enable && Build.VERSION.SDK_INT >= 33) {
                                        notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.toggleNotificationShortcuts(enable)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBackground,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                        }

                        Text(
                            text = "يعرض شريط وصول سريع في مركز إشعارات أندرويد لتشغيل التطبيقات المفضلة (اتصال، كاميرا، رسائل، متصفح) بضغطة زر واحدة بدون الحاجة لفتح التطبيق.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "اضغط على أيقونة 🔔 بجانب أي تطبيق لتثبيته/إلغاء تثبيته",
                                fontSize = 10.sp,
                                color = PrimaryCyan
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✨ تخصيص التطبيقات والمزدوجة أوفلاين",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PrimaryCyan
                        )
                        Text(
                            text = "يمكنك تغيير شكل كل أيقونة (دائري، سكويركل، سداسي)، لونها، وتسميتها المخصصة، أو تفعيل وضع التطبيق المزدوج لحسابات العمل، وإخفاء التطبيقات الحساسة في الخزنة.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Quick 1-Tap Presets Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚡ إضافة سريعة من التطبيقات الشائعة المدمجة:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF34D399)
                        )

                        androidx.compose.foundation.layout.FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.data.model.PopularAppPresets.presets.forEach { preset ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        viewModel.addShortcut(preset.copy(id = java.util.UUID.randomUUID().toString()))
                                        viewModel.showToast("تمت إضافة اختصار \"${preset.displayLabel}\"")
                                    },
                                    label = { Text("+ ${preset.displayLabel}", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = DarkSurface,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            items(shortcuts, key = { it.id }) { shortcut ->
                ShortcutItemCard(
                    shortcut = shortcut,
                    isPinnedToNotif = pinnedNotificationShortcuts.contains(shortcut.id),
                    onTogglePinNotif = { viewModel.togglePinnedNotificationShortcut(shortcut.id) },
                    onEdit = {
                        editingShortcut = shortcut
                        isNewDialog = false
                    },
                    onDelete = { viewModel.deleteShortcut(shortcut.id) },
                    onToggleHide = { viewModel.toggleHideShortcut(shortcut.id) },
                    onLaunch = { viewModel.launchAppShortcut(shortcut) },
                    onPin = { viewModel.pinShortcutToHomeScreen(shortcut) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Edit / Create Dialog
    if (editingShortcut != null) {
        ShortcutEditDialog(
            shortcut = editingShortcut!!,
            isNew = isNewDialog,
            onDismiss = { editingShortcut = null },
            onSave = { updated ->
                viewModel.addShortcut(updated)
                editingShortcut = null
            }
        )
    }
}

@Composable
fun ShortcutItemCard(
    shortcut: AppShortcut,
    isPinnedToNotif: Boolean = false,
    onTogglePinNotif: () -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleHide: () -> Unit,
    onLaunch: () -> Unit,
    onPin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPinnedToNotif) Color(0xFF10B981).copy(alpha = 0.6f) else if (shortcut.isDualApp) Color(0xFF10B981).copy(alpha = 0.4f) else DarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon with custom shape
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                ShortcutIconBadge(shortcut = shortcut)

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = shortcut.displayLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )

                        if (isPinnedToNotif) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "بالإشعارات 📌",
                                    color = Color(0xFF34D399),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (shortcut.isDualApp) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = shortcut.dualLabel,
                                    color = Color(0xFF34D399),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (shortcut.isHidden) {
                            Surface(
                                color = Color(0xFFF43F5E).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "مخفي 🔒",
                                    color = Color(0xFFFB7185),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "الشكل: ${shortcut.iconShape.labelAr}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onTogglePinNotif, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isPinnedToNotif) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Pin to Notifications",
                        tint = if (isPinnedToNotif) Color(0xFF34D399) else TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(onClick = onPin, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.PushPin, contentDescription = "Pin", tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onLaunch, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Launch", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onToggleHide, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (shortcut.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Hide",
                        tint = if (shortcut.isHidden) Color(0xFFF43F5E) else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ShortcutIconBadge(shortcut: AppShortcut) {
    val iconColor = Color(shortcut.iconColor)
    val shape = when (shortcut.iconShape) {
        IconShape.ROUND -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(16.dp)
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(8.dp)
        IconShape.HEXAGON -> RoundedCornerShape(12.dp)
        IconShape.NEON_GLOW -> RoundedCornerShape(14.dp)
    }

    Surface(
        shape = shape,
        color = iconColor.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, iconColor),
        modifier = Modifier.size(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            val vector = getIconVector(shortcut.iconSymbol)
            Icon(
                imageVector = vector,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun getIconVector(symbol: String): ImageVector = when (symbol) {
    "Phone" -> Icons.Default.Call
    "Chat" -> Icons.Default.Chat
    "Camera" -> Icons.Default.CameraAlt
    "Browser" -> Icons.Default.Language
    "Music" -> Icons.Default.MusicNote
    "Notes" -> Icons.Default.Note
    "Tools" -> Icons.Default.Build
    else -> Icons.Default.Language
}

@Composable
fun ShortcutEditDialog(
    shortcut: AppShortcut,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (AppShortcut) -> Unit
) {
    var name by remember { mutableStateOf(shortcut.name) }
    var customLabel by remember { mutableStateOf(shortcut.customLabel) }
    var selectedShape by remember { mutableStateOf(shortcut.iconShape) }
    var selectedColor by remember { mutableStateOf(shortcut.iconColor) }
    var selectedSymbol by remember { mutableStateOf(shortcut.iconSymbol) }
    var isDualApp by remember { mutableStateOf(shortcut.isDualApp) }
    var dualLabel by remember { mutableStateOf(shortcut.dualLabel) }
    var isHidden by remember { mutableStateOf(shortcut.isHidden) }

    val colorsList = listOf(
        0xFF38BDF8, 0xFF10B981, 0xFFF59E0B, 0xFFF43F5E, 0xFFA855F7, 0xFFE2E8F0, 0xFF06B6D4
    )
    val symbolsList = listOf("Phone", "Chat", "Camera", "Browser", "Music", "Notes", "Tools")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "إضافة اختصار جديد" else "تعديل شكل الاختصار",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text("اسم الاختصار المخصص") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("شكل الأيقونة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconShape.values().take(3).forEach { shape ->
                        FilterChip(
                            selected = selectedShape == shape,
                            onClick = { selectedShape = shape },
                            label = { Text(shape.labelAr, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryCyan.copy(alpha = 0.25f))
                        )
                    }
                }

                Text("رمز الأيقونة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    symbolsList.take(5).forEach { sym ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedSymbol == sym) PrimaryCyan.copy(alpha = 0.3f) else DarkSurfaceVariant,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedSymbol = sym }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(getIconVector(sym), contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Text("لون الأيقونة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    colorsList.forEach { col ->
                        Surface(
                            shape = CircleShape,
                            color = Color(col),
                            border = androidx.compose.foundation.BorderStroke(
                                if (selectedColor == col) 2.dp else 0.dp,
                                Color.White
                            ),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { selectedColor = col }
                        ) {}
                    }
                }

                // Dual App Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("تطبيق مزدوج (Dual App)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text("يضيف شارة النسخة الثانية", fontSize = 10.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isDualApp,
                        onCheckedChange = { isDualApp = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = Color(0xFF10B981))
                    )
                }

                // Hide App Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إخفاء التطبيق في الخزنة السرية", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text("حماية برمز PIN", fontSize = 10.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isHidden,
                        onCheckedChange = { isHidden = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = Color(0xFFF43F5E))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = shortcut.copy(
                        name = customLabel.ifBlank { name },
                        customLabel = customLabel,
                        iconShape = selectedShape,
                        iconColor = selectedColor,
                        iconSymbol = selectedSymbol,
                        isDualApp = isDualApp,
                        dualLabel = dualLabel,
                        isHidden = isHidden
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text("حفظ", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}
