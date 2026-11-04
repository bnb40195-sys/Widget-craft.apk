package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.PopularAppPresets
import com.example.data.model.WidgetConfig
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.components.WidgetPreviewCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allWidgets by viewModel.allWidgets.collectAsState()
    val activeWidget by viewModel.activeLauncherWidget.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val notifShortcutsEnabled by viewModel.notificationShortcutsEnabled.collectAsState()

    var editingShortcutForHome by remember { mutableStateOf<AppShortcut?>(null) }
    var isNewShortcutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "WidgetCraft",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "صانع ومحرر الويدجات والاختصارات (أوفلاين)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    OfflineStatusBadge(
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // AI Studio
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(AppScreen.AiImageWidgetStudio) },
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_ai_studio")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Studio")
                }

                // All Widgets
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(AppScreen.AllWidgets) },
                    containerColor = DarkSurfaceVariant,
                    contentColor = PrimaryCyan,
                    modifier = Modifier.testTag("fab_all_widgets")
                ) {
                    Icon(imageVector = Icons.Default.Widgets, contentDescription = "All Widgets")
                }

                // Create New Widget Button
                ExtendedFloatingActionButton(
                    onClick = { viewModel.navigateTo(AppScreen.Editor()) },
                    containerColor = PrimaryCyan,
                    contentColor = DarkBackground,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("صنع ويدجت جديد", fontWeight = FontWeight.Black) },
                    modifier = Modifier.testTag("btn_create_widget")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Action Banner
            item {
                QuickActionBanner(
                    onAllWidgetsClick = { viewModel.navigateTo(AppScreen.AllWidgets) },
                    onAiStudioClick = { viewModel.navigateTo(AppScreen.AiImageWidgetStudio) },
                    onKeyboardClick = { viewModel.navigateTo(AppScreen.KeyboardStudio) },
                    onWallpaperClick = { viewModel.navigateTo(AppScreen.WallpaperStudio) },
                    onShortcutsClick = { viewModel.navigateTo(AppScreen.ShortcutsStudio) },
                    onSandboxClick = { viewModel.navigateTo(AppScreen.LiveSandbox) },
                    onVaultClick = { viewModel.navigateTo(AppScreen.HiddenAppsVault) },
                    onAlertsClick = { viewModel.navigateTo(AppScreen.AlertsStudio) }
                )
            }

            // Direct Home Screen Add without files Guide Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إضافة ويدجت من الشاشة الرئيسية بدون ملفات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "اضغط مطولاً على أي مساحة فارغة في شاشة هاتفك الرئيسية، ثم اختر (الويدجات / Widgets) -> WidgetCraft، لتثبيت الويدجت فوراً بدون أي ملفات خارجية!",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Active Home Screen Widget Banner (ويدجت مجالات / الويدجت النشط الرئيسي فقط)
            item {
                ActiveWidgetShowcaseSection(
                    activeWidget = activeWidget,
                    onPin = { widget -> viewModel.pinToHomeScreen(widget) },
                    onEdit = { widget -> viewModel.navigateTo(AppScreen.Editor(widgetId = widget.id)) },
                    onBrowseAll = { viewModel.navigateTo(AppScreen.AllWidgets) }
                )
            }

            // Dedicated Universal App Shortcuts Section (قسم إضافة أي إختصار لأي تطبيق)
            item {
                AppShortcutsHomeSection(
                    shortcuts = shortcuts.filterNot { it.isHidden },
                    onAddNew = {
                        editingShortcutForHome = AppShortcut(
                            name = "تطبيق جديد",
                            customLabel = "اختصار",
                            packageName = "",
                            iconColor = 0xFF38BDF8
                        )
                        isNewShortcutDialog = true
                    },
                    onQuickPresetAdd = { preset ->
                        viewModel.addShortcut(preset.copy(id = UUID.randomUUID().toString()))
                        viewModel.showToast("تمت إضافة اختصار \"${preset.displayLabel}\" بنجاح ✨")
                    },
                    onOpenStudio = { viewModel.navigateTo(AppScreen.ShortcutsStudio) },
                    onLaunch = { shortcut -> viewModel.launchAppShortcut(shortcut) },
                    onPin = { shortcut -> viewModel.pinShortcutToHomeScreen(shortcut) },
                    onEdit = { shortcut ->
                        editingShortcutForHome = shortcut
                        isNewShortcutDialog = false
                    },
                    onDelete = { shortcut ->
                        viewModel.deleteShortcut(shortcut.id)
                    }
                )
            }

            // Navigation Hub Cards: AI Image-to-Widget Studio, All Widgets & Online Store
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Featured Card: AI Image-to-Widget Studio (جديد: حوّل صورتك إلى ويدجت بالذكاء الاصطناعي أوفلاين)
                    Card(
                        onClick = { viewModel.navigateTo(AppScreen.AiImageWidgetStudio) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B).copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFA855F7).copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFC4B5FD),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "استوديو الذكاء الاصطناعي للصور",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFA855F7).copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "ميزة ذكية 🪄",
                                            color = Color(0xFFC4B5FD),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "ارفع أي صورة ويقوم الذكاء الاصطناعي بمطابقتها وتوليد ويدجت يعمل بدون نت 100% ومفلتر شرعياً!",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Card 1: All Widgets Hub
                    Card(
                        onClick = { viewModel.navigateTo(AppScreen.AllWidgets) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PrimaryCyan.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Widgets,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "قسم عرض جميع الويدجات (${allWidgets.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "استعراض، تعديل، تفعيل للشاشة، نسخ ومشاركة جميع الويدجات المحفوظة أوفلاين.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Card 2: AI Keyboard & Wallpaper Studio
                    Card(
                        onClick = { viewModel.navigateTo(AppScreen.KeyboardStudio) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "توليد لوحة المفاتيح والخلفيات بالذكاء 🪄",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "أوفلاين 100%",
                                            color = Color(0xFF34D399),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "أضف أي صورة ليقوم الذكاء الاصطناعي بمطابقة ألوانها فوراً وتوليد لوحة مفاتيح تفاعلية للكتابة، وخلفيات شاشة محسنة بدون إنترنت.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Card 3: Notification Center App Shortcuts
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Apps,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "إختصارات مركز الإشعارات ⚡",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = if (notifShortcutsEnabled) "الشريط نشط الآن في شريط الحالة" else "الشريط متوقف (اضغط للتفعيل)",
                                        fontSize = 11.sp,
                                        color = if (notifShortcutsEnabled) Color(0xFF34D399) else TextSecondary
                                    )
                                }
                                Switch(
                                    checked = notifShortcutsEnabled,
                                    onCheckedChange = { viewModel.toggleNotificationShortcuts(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DarkBackground,
                                        checkedTrackColor = Color(0xFF10B981)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "شريط وصول سريع دائم في مركز الإشعارات بهواتف أندرويد لتشغيل تطبيقاتك المفضلة (اتصال، كاميرا، رسائل، متصفح) بضغطة زر واحدة.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.ShortcutsStudio) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تخصيص التطبيقات المثبتة بالإشعارات", color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Spacing for Floating Buttons
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Add / Edit Shortcut Dialog
    if (editingShortcutForHome != null) {
        val target = editingShortcutForHome!!
        AddOrEditShortcutDialog(
            initialShortcut = target,
            isNew = isNewShortcutDialog,
            onDismiss = { editingShortcutForHome = null },
            onConfirm = { saved ->
                if (isNewShortcutDialog) {
                    viewModel.addShortcut(saved)
                } else {
                    viewModel.updateShortcut(saved)
                }
                editingShortcutForHome = null
            }
        )
    }
}

@Composable
private fun QuickActionBanner(
    onAllWidgetsClick: () -> Unit,
    onAiStudioClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    onWallpaperClick: () -> Unit,
    onShortcutsClick: () -> Unit,
    onSandboxClick: () -> Unit,
    onVaultClick: () -> Unit,
    onAlertsClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionPill(
            title = "جميع الويدجات",
            subtitle = "عرض وإدارة",
            icon = Icons.Default.Widgets,
            tint = PrimaryCyan,
            onClick = onAllWidgetsClick
        )

        QuickActionPill(
            title = "استوديو الذكاء 🪄",
            subtitle = "توليد من صورة",
            icon = Icons.Default.AutoAwesome,
            tint = Color(0xFF8B5CF6),
            onClick = onAiStudioClick
        )

        QuickActionPill(
            title = "لوحة المفاتيح",
            subtitle = "تشغيل وسمات",
            icon = Icons.Default.Keyboard,
            tint = Color(0xFF06B6D4),
            onClick = onKeyboardClick
        )

        QuickActionPill(
            title = "مركز الإشعارات",
            subtitle = "إختصارات سريعة",
            icon = Icons.Default.Apps,
            tint = Color(0xFF10B981),
            onClick = onShortcutsClick
        )

        QuickActionPill(
            title = "محاكي الشاشة",
            subtitle = "معاينة حية",
            icon = Icons.Default.GridView,
            tint = Color(0xFF38BDF8),
            onClick = onSandboxClick
        )

        QuickActionPill(
            title = "الخلفيات والصور",
            subtitle = "إضافة من الخارج",
            icon = Icons.Default.Wallpaper,
            tint = Color(0xFFEC4899),
            onClick = onWallpaperClick
        )

        QuickActionPill(
            title = "لوحة المفاتيح",
            subtitle = "سمات خارجية",
            icon = Icons.Default.Keyboard,
            tint = Color(0xFF06B6D4),
            onClick = onKeyboardClick
        )

        QuickActionPill(
            title = "خزنة الإخفاء",
            subtitle = "حماية بـ PIN",
            icon = Icons.Default.Lock,
            tint = Color(0xFFF43F5E),
            onClick = onVaultClick
        )

        QuickActionPill(
            title = "التنبيهات والمواعيد",
            subtitle = "تذكيرات أوفلاين",
            icon = Icons.Default.Alarm,
            tint = Color(0xFFEAB308),
            onClick = onAlertsClick
        )
    }
}

@Composable
private fun QuickActionPill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.width(155.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tint.copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ActiveWidgetShowcaseSection(
    activeWidget: WidgetConfig?,
    onPin: (WidgetConfig) -> Unit,
    onEdit: (WidgetConfig) -> Unit,
    onBrowseAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryCyan.copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الويدجت النشط الرئيسي على هاتفك",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryCyan
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "يعمل بدون نت ⚡",
                        fontSize = 10.sp,
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (activeWidget != null) {
                // Interactive Preview Card
                WidgetPreviewCard(
                    config = activeWidget,
                    isInteractive = true,
                    onClick = { onEdit(activeWidget) }
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPin(activeWidget) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تثبيت على الشاشة الرئيسية",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { onEdit(activeWidget) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "تعديل", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            } else {
                // Empty active state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "لم يتم تحديد ويدجت نشط بعد",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "اختر ويدجت من قسم (جميع الويدجات) لتثبيته كويدجت رئيسي لشاشتك.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onBrowseAll,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("فتح جميع الويدجات 📱", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AppShortcutsHomeSection(
    shortcuts: List<AppShortcut>,
    onAddNew: () -> Unit,
    onQuickPresetAdd: (AppShortcut) -> Unit,
    onOpenStudio: () -> Unit,
    onLaunch: (AppShortcut) -> Unit,
    onPin: (AppShortcut) -> Unit,
    onEdit: (AppShortcut) -> Unit,
    onDelete: (AppShortcut) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryCyan.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "قسم إضافة أي إختصار لأي تطبيق",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "أشكال مخصصة • تطبيقات مزدوجة • إطلاق فوري وتثبيت للشاشة",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(onClick = onOpenStudio) {
                    Text("الإعدادات الكاملة", color = PrimaryCyan, fontSize = 11.sp)
                }
            }

            // Quick Add Preset Chips (Over 20 popular apps)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "⚡ إضافة سريعة من أشهر التطبيقات بلمسة واحدة:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF34D399)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PopularAppPresets.presets) { preset ->
                        FilterChip(
                            selected = false,
                            onClick = { onQuickPresetAdd(preset) },
                            label = {
                                Text(
                                    text = if (preset.isDualApp) "⚡ ${preset.displayLabel}" else preset.displayLabel,
                                    fontSize = 11.sp
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(preset.iconColor), CircleShape)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // Add Custom Any App Button
            Button(
                onClick = onAddNew,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "➕ إضافة اختصار لأي تطبيق في الهاتف (مخصص)",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Current Shortcuts List
            if (shortcuts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "الاختصارات النشطة (${shortcuts.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    shortcuts.forEach { shortcut ->
                        ShortcutHomeRowItem(
                            shortcut = shortcut,
                            onLaunch = { onLaunch(shortcut) },
                            onPin = { onPin(shortcut) },
                            onEdit = { onEdit(shortcut) },
                            onDelete = { onDelete(shortcut) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutHomeRowItem(
    shortcut: AppShortcut,
    onLaunch: () -> Unit,
    onPin: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon shape container
            val shape = when (shortcut.iconShape) {
                IconShape.ROUND -> CircleShape
                IconShape.SQUIRCLE -> RoundedCornerShape(12.dp)
                IconShape.ROUNDED_SQUARE -> RoundedCornerShape(6.dp)
                IconShape.HEXAGON -> RoundedCornerShape(10.dp)
                IconShape.NEON_GLOW -> RoundedCornerShape(8.dp)
            }

            Surface(
                shape = shape,
                color = Color(shortcut.iconColor),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val iconVector = when (shortcut.iconSymbol) {
                        "Phone" -> Icons.Default.Call
                        "Chat" -> Icons.Default.Chat
                        "Camera" -> Icons.Default.CameraAlt
                        "Browser" -> Icons.Default.Language
                        "Music" -> Icons.Default.MusicNote
                        "Tools" -> Icons.Default.Build
                        "Notes" -> Icons.Default.Note
                        else -> Icons.Default.PlayArrow
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = shortcut.displayLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    if (shortcut.isDualApp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = shortcut.dualLabel.ifBlank { "مزدوج" },
                                color = Color(0xFFF59E0B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = if (shortcut.packageName.isNotBlank()) shortcut.packageName else "تطبيق محلي",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pin to Launcher
                IconButton(onClick = onPin, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pin to Home",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Launch
                IconButton(onClick = onLaunch, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Launch",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Edit
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddOrEditShortcutDialog(
    initialShortcut: AppShortcut,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (AppShortcut) -> Unit
) {
    var name by remember { mutableStateOf(initialShortcut.name) }
    var customLabel by remember { mutableStateOf(initialShortcut.customLabel) }
    var packageName by remember { mutableStateOf(initialShortcut.packageName) }
    var iconShape by remember { mutableStateOf(initialShortcut.iconShape) }
    var iconColor by remember { mutableStateOf(initialShortcut.iconColor) }
    var iconSymbol by remember { mutableStateOf(initialShortcut.iconSymbol) }
    var isDualApp by remember { mutableStateOf(initialShortcut.isDualApp) }
    var dualLabel by remember { mutableStateOf(initialShortcut.dualLabel) }

    val colorOptions = listOf(
        0xFF38BDF8, 0xFF25D366, 0xFFFF0000, 0xFF1877F2, 0xFFE1306C,
        0xFFF59E0B, 0xFF10B981, 0xFF8B5CF6, 0xFFEC4899, 0xFF050505
    )

    val symbolOptions = listOf("PlayArrow", "Chat", "Phone", "Camera", "Browser", "Music", "Tools", "Notes")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "إضافة اختصار لأي تطبيق" else "تعديل شكل الاختصار",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم التطبيق الأصلي") },
                    placeholder = { Text("مثلاً: WhatsApp أو Telegram") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Custom Label
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text("الاسم المخصص المعروض") },
                    placeholder = { Text("الاسم الذي سيظهر تحت الأيقونة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Package Name
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("معرّف الحزمة Package Name (اختياري)") },
                    placeholder = { Text("مثلاً: com.whatsapp") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Icon Shape Selection
                Text(text = "شكل الأيقونة:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconShape.values().forEach { shape ->
                        FilterChip(
                            selected = iconShape == shape,
                            onClick = { iconShape = shape },
                            label = { Text(shape.labelAr, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan.copy(alpha = 0.25f),
                                selectedLabelColor = PrimaryCyan
                            )
                        )
                    }
                }

                // Icon Color Selection
                Text(text = "لون الأيقونة:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { colorVal ->
                        val isSelected = iconColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { iconColor = colorVal }
                        )
                    }
                }

                // Icon Symbol Selection
                Text(text = "رمز الأيقونة:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    symbolOptions.forEach { sym ->
                        FilterChip(
                            selected = iconSymbol == sym,
                            onClick = { iconSymbol = sym },
                            label = { Text(sym, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan.copy(alpha = 0.25f),
                                selectedLabelColor = PrimaryCyan
                            )
                        )
                    }
                }

                // Dual App / Clone Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "تطبيق مزدوج (Clone / Dual App)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text(text = "إضافة شارة تدل على نسخة ثانية من التطبيق", fontSize = 10.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isDualApp,
                        onCheckedChange = { isDualApp = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryCyan)
                    )
                }

                if (isDualApp) {
                    OutlinedTextField(
                        value = dualLabel,
                        onValueChange = { dualLabel = it },
                        label = { Text("شارة النسخة المزدوجة") },
                        placeholder = { Text("مثلاً: نسخة 2 أو عمل") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalShortcut = initialShortcut.copy(
                        name = name.ifBlank { "تطبيق" },
                        customLabel = customLabel,
                        packageName = packageName.trim(),
                        iconShape = iconShape,
                        iconColor = iconColor,
                        iconSymbol = iconSymbol,
                        isDualApp = isDualApp,
                        dualLabel = dualLabel
                    )
                    onConfirm(finalShortcut)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text(if (isNew) "إضافة وحفظ" else "حفظ التعديلات", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary
    )
}
