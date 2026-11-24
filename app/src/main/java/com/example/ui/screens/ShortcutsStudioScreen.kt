package com.example.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.browser.BrowserLanguage
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.PopularAppPresets
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import java.util.UUID

private val ActiveZoneBg = Color(0xFF20232C)
private val InactiveZoneBg = Color(0xFF121419)
private val EditorBg = Color(0xFF0E1015)
private val EditorCardBg = Color(0xFF1C1E26)
private val DefaultShortcutBlue = Color(0xFF2D6FE4)
private val RedFabColor = Color(0xFFC53929)

private val ShortcutFrameStyles = listOf(
    "WHITE_BOX",
    "BARE_LARGE",
    "BARE_SMALL",
    "SOLID_WHITE",
    "OUTLINE_ROUNDED",
    "THICK_ROUNDED",
    "DOUBLE_ROUNDED",
    "CIRCLE_BADGE"
)

private val ShortcutSymbolsPalette = listOf(
    "Ayah",
    "Settings",
    "Calendar",
    "Tools",
    "PlayArrow",
    "Tiles",
    "Clock",
    "Translate",
    "Phone",
    "Chat",
    "Browser",
    "Music",
    "Notes",
    "Bookmark",
    "Shield",
    "App"
)

private val ShortcutColorPalette = listOf(
    0xFF2D6FE4L,
    0xFF0284C7L,
    0xFF10B981L,
    0xFFF59E0BL,
    0xFFEA580CL,
    0xFFA855F7L,
    0xFFEF4444L
)

@OptIn(ExperimentalMaterial3Api::class)
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
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isEn = appLanguage == BrowserLanguage.ENGLISH

    val activeShortcuts = remember(shortcuts) {
        shortcuts.filter { it.isActive && !it.isHidden }.sortedBy { it.layerIndex }
    }
    val inactiveShortcuts = remember(shortcuts) {
        shortcuts.filter { !it.isActive || it.isHidden }
    }

    var editingShortcut by remember { mutableStateOf<AppShortcut?>(null) }
    var isNewDialog by remember { mutableStateOf(false) }
    var showBrowseInstalledApps by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleNotificationShortcuts(true)
        } else {
            viewModel.showToast(
                if (isEn) "Notification permission is needed to enable shortcut bar"
                else "يلزم إذن الإشعارات لتفعيل شريط الإختصارات"
            )
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = InactiveZoneBg,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEn) "App Shortcuts" else "إختصارات تطبيقات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFFF1F5F9)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("btn_back_shortcuts")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isEn) "Back" else "رجوع",
                                tint = Color(0xFFF1F5F9)
                            )
                        }
                    },
                    actions = {
                        // Notification Shortcuts quick toggle
                        IconButton(
                            onClick = {
                                val next = !notifShortcutsEnabled
                                if (next && Build.VERSION.SDK_INT >= 33) {
                                    notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleNotificationShortcuts(next)
                                }
                            },
                            modifier = Modifier.testTag("btn_toggle_notif_shortcuts")
                        ) {
                            Icon(
                                imageVector = if (notifShortcutsEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = if (isEn) "Notification Bar" else "شريط الإشعارات",
                                tint = if (notifShortcutsEnabled) Color(0xFF34D399) else Color(0xFF9CA3AF)
                            )
                        }

                        // Installed device apps browser
                        IconButton(
                            onClick = { showBrowseInstalledApps = true },
                            modifier = Modifier.testTag("btn_browse_installed_apps")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = if (isEn) "Installed Apps" else "تطبيقات الهاتف",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ActiveZoneBg)
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingShortcut = AppShortcut(
                            name = if (isEn) "New Shortcut" else "آية ويدجت",
                            customLabel = if (isEn) "New Shortcut" else "آية ويدجت",
                            iconColor = 0xFF2D6FE4L,
                            iconSymbol = "Ayah",
                            iconStyle = "OUTLINE_ROUNDED",
                            isActive = true,
                            layerIndex = (activeShortcuts.maxOfOrNull { it.layerIndex } ?: 0) + 1
                        )
                        isNewDialog = true
                    },
                    shape = CircleShape,
                    containerColor = RedFabColor,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .navigationBarsPadding()
                        .testTag("btn_add_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isEn) "Add Shortcut" else "إضافة اختصار",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // TOP ZONE: Active Shortcuts (الاختصارات النشطه)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ActiveZoneBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = if (isEn) "Active Shortcuts" else "الاختصارات النشطه",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB8BDC9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp)
                    )

                    if (activeShortcuts.isEmpty()) {
                        Text(
                            text = if (isEn) "No active shortcuts yet. Tap + to add." else "لا توجد اختصارات نشطة. اضغط + للإضافة.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )
                    } else {
                        ShortcutFourColumnGrid(
                            items = activeShortcuts,
                            useArabicNumerals = useArabicNumerals,
                            isEnglish = isEn,
                            showLayerLabel = true,
                            onTap = { shortcut ->
                                editingShortcut = shortcut
                                isNewDialog = false
                            },
                            onLongPress = { shortcut ->
                                viewModel.launchAppShortcut(shortcut)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // BOTTOM ZONE: Inactive Shortcuts (الاختصارات الغير نشطة)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSizeOrFill()
                        .background(InactiveZoneBg)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = if (isEn) "Inactive Shortcuts" else "الاختصارات الغير نشطة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB8BDC9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp)
                    )

                    if (inactiveShortcuts.isEmpty()) {
                        Text(
                            text = if (isEn) "No inactive shortcuts" else "لا توجد اختصارات غير نشطة",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        )
                    } else {
                        ShortcutFourColumnGrid(
                            items = inactiveShortcuts,
                            useArabicNumerals = useArabicNumerals,
                            isEnglish = isEn,
                            showLayerLabel = false,
                            onTap = { shortcut ->
                                editingShortcut = shortcut
                                isNewDialog = false
                            },
                            onLongPress = { shortcut ->
                                viewModel.launchAppShortcut(shortcut)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }

    // Installed Apps Picker Dialog
    if (showBrowseInstalledApps) {
        InstalledAppsPickerDialog(
            viewModel = viewModel,
            isEnglish = isEn,
            useArabicNumerals = useArabicNumerals,
            onDismiss = { showBrowseInstalledApps = false },
            onAppPicked = { picked ->
                viewModel.addShortcut(picked.copy(id = UUID.randomUUID().toString(), isActive = true))
                showBrowseInstalledApps = false
            }
        )
    }

    // Full-Screen Dark Editor Matching Screenshot 2
    if (editingShortcut != null) {
        ShortcutEditorFullScreenDialog(
            shortcut = editingShortcut!!,
            isNew = isNewDialog,
            viewModel = viewModel,
            isEnglish = isEn,
            useArabicNumerals = useArabicNumerals,
            isPinnedToNotif = pinnedNotificationShortcuts.contains(editingShortcut!!.id),
            onDismiss = { editingShortcut = null },
            onDelete = { id ->
                if (!isNewDialog) {
                    viewModel.deleteShortcut(id)
                }
                editingShortcut = null
            },
            onSave = { updated ->
                viewModel.addShortcut(updated)
                editingShortcut = null
            }
        )
    }
}

private fun Modifier.defaultMinSizeOrFill(): Modifier = this.heightIn(min = 380.dp)

@Composable
private fun ShortcutFourColumnGrid(
    items: List<AppShortcut>,
    useArabicNumerals: Boolean,
    isEnglish: Boolean,
    showLayerLabel: Boolean,
    onTap: (AppShortcut) -> Unit,
    onLongPress: (AppShortcut) -> Unit
) {
    val rows = remember(items) { items.chunked(4) }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (colIdx in 0 until 4) {
                    val item = rowItems.getOrNull(colIdx)
                    if (item != null) {
                        Box(modifier = Modifier.weight(1f)) {
                            ShortcutGridItemTile(
                                shortcut = item,
                                useArabicNumerals = useArabicNumerals,
                                isEnglish = isEnglish,
                                showLayerLabel = showLayerLabel,
                                onTap = { onTap(item) },
                                onLongPress = { onLongPress(item) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortcutGridItemTile(
    shortcut: AppShortcut,
    useArabicNumerals: Boolean,
    isEnglish: Boolean,
    showLayerLabel: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongPress
            )
            .padding(vertical = 4.dp)
            .testTag("shortcut_tile_${shortcut.id}")
    ) {
        ShortcutStyledCircleAvatar(
            iconColor = Color(shortcut.iconColor),
            iconSymbol = shortcut.iconSymbol,
            iconStyle = shortcut.iconStyle,
            size = 56.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = shortcut.displayLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFE5E7EB),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (showLayerLabel) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = shortcut.getLayerLabel(useArabicNumerals, isEnglish),
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShortcutEditorFullScreenDialog(
    shortcut: AppShortcut,
    isNew: Boolean,
    viewModel: MainViewModel,
    isEnglish: Boolean = false,
    useArabicNumerals: Boolean = false,
    isPinnedToNotif: Boolean = false,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onSave: (AppShortcut) -> Unit
) {
    var name by remember { mutableStateOf(shortcut.name) }
    var customLabel by remember { mutableStateOf(shortcut.displayLabel) }
    var packageName by remember { mutableStateOf(shortcut.packageName) }
    var externalUrl by remember { mutableStateOf(shortcut.externalUrl) }
    var selectedShape by remember { mutableStateOf(shortcut.iconShape) }
    var selectedColor by remember { mutableLongStateOf(shortcut.iconColor) }
    var selectedSymbol by remember { mutableStateOf(shortcut.iconSymbol) }
    var selectedStyle by remember { mutableStateOf(shortcut.iconStyle) }
    var isActive by remember { mutableStateOf(shortcut.isActive) }
    var layerIndex by remember { mutableIntStateOf(shortcut.layerIndex.coerceAtLeast(1)) }
    var isDualApp by remember { mutableStateOf(shortcut.isDualApp) }
    var isHidden by remember { mutableStateOf(shortcut.isHidden) }

    var sourceCardExpanded by remember { mutableStateOf(isNew) }
    var iconCardExpanded by remember { mutableStateOf(true) }
    var showInstalledAppsInEditor by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (isEnglish) LayoutDirection.Ltr else LayoutDirection.Rtl
        ) {
            Scaffold(
                containerColor = EditorBg,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close X in Red (Top-start in RTL, Top-right visually like Image 2)
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("editor_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isEnglish) "Close" else "إغلاق",
                                tint = Color(0xFFD93829),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Delete Trash Icon in White
                        IconButton(
                            onClick = { onDelete(shortcut.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("editor_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = if (isEnglish) "Delete" else "حذف",
                                tint = Color(0xFFE5E7EB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EditorBg)
                            .navigationBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                val finalLabel = customLabel.trim().ifBlank { name.ifBlank { if (isEnglish) "Shortcut" else "اختصار" } }
                                val updated = shortcut.copy(
                                    name = finalLabel,
                                    customLabel = finalLabel,
                                    packageName = packageName.trim(),
                                    externalUrl = externalUrl.trim(),
                                    iconShape = selectedShape,
                                    iconColor = selectedColor,
                                    iconSymbol = selectedSymbol,
                                    iconStyle = selectedStyle,
                                    isActive = isActive,
                                    layerIndex = layerIndex,
                                    isDualApp = isDualApp,
                                    isHidden = isHidden
                                )
                                onSave(updated)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(selectedColor)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("editor_save_button")
                        ) {
                            Text(
                                text = if (isEnglish) "Save Shortcut" else "حفظ الاختصار",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Centered Circular Preview + Title (exact Image 2 Hero)
                    ShortcutStyledCircleAvatar(
                        iconColor = Color(selectedColor),
                        iconSymbol = selectedSymbol,
                        iconStyle = selectedStyle,
                        size = 58.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = customLabel.ifBlank { if (isEnglish) "New Shortcut" else "آية ويدجت" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE5E7EB),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtle quick actions strip (Launch, Active/Layer, Pin)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isNew) {
                            Surface(
                                onClick = {
                                    viewModel.launchAppShortcut(
                                        shortcut.copy(
                                            customLabel = customLabel,
                                            packageName = packageName,
                                            externalUrl = externalUrl
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFF1C2333)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                                    Text(if (isEnglish) "Launch" else "تشغيل", color = Color(0xFFE5E7EB), fontSize = 11.sp)
                                }
                            }
                        }

                        Surface(
                            onClick = { isActive = !isActive },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isActive) Color(0xFF2D6FE4).copy(alpha = 0.22f) else Color(0xFF1F2430)
                        ) {
                            val layerText = NumeralFormatter.formatNumber(layerIndex, useArabicNumerals)
                            Text(
                                text = if (isActive) {
                                    if (isEnglish) "Active • Layer $layerText" else "نشط • طبقة $layerText"
                                } else {
                                    if (isEnglish) "Inactive" else "غير نشط"
                                },
                                color = if (isActive) Color(0xFF60A5FA) else Color(0xFF9CA3AF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        if (!isNew) {
                            Surface(
                                onClick = { viewModel.togglePinnedNotificationShortcut(shortcut.id) },
                                shape = RoundedCornerShape(18.dp),
                                color = if (isPinnedToNotif) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF1F2430)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = if (isPinnedToNotif) Color(0xFF34D399) else Color(0xFF9CA3AF),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isEnglish) "Notif" else "الإشعارات",
                                        color = if (isPinnedToNotif) Color(0xFF34D399) else Color(0xFF9CA3AF),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // ================= CARD 1: APP / SOURCE PICKER CARD (Image 2 Card 1) =================
                    Surface(
                        color = EditorCardBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sourceCardExpanded = !sourceCardExpanded }
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = if (isEnglish) "Select App" else "اختيار التطبيق",
                                        tint = Color(0xFFB8BDC9),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = when {
                                            externalUrl.isNotBlank() -> externalUrl
                                            packageName.isNotBlank() -> packageName
                                            else -> if (isEnglish) "Choose app, preset, or URL" else "اختيار التطبيق أو الرابط والمصدر"
                                        },
                                        color = Color(0xFFD1D5DB),
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = if (sourceCardExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFFB8BDC9),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            AnimatedVisibility(visible = sourceCardExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Pick installed app button
                                    Button(
                                        onClick = { showInstalledAppsInEditor = true },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262A36)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Apps, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isEnglish) "Browse Installed Phone Apps" else "استعراض تطبيقات الهاتف المثبتة",
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Layer & Active selector row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Active Tile & Layer" else "تفعيل الاختصار ورقم الطبقة",
                                            color = Color(0xFFB8BDC9),
                                            fontSize = 12.sp
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            IconButton(
                                                onClick = { if (layerIndex > 1) layerIndex-- },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                            Text(
                                                text = NumeralFormatter.formatNumber(layerIndex, useArabicNumerals),
                                                color = Color(0xFF60A5FA),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            IconButton(
                                                onClick = { layerIndex++ },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                            Switch(
                                                checked = isActive,
                                                onCheckedChange = { isActive = it },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color.White,
                                                    checkedTrackColor = DefaultShortcutBlue
                                                )
                                            )
                                        }
                                    }

                                    // Package & URL fields
                                    OutlinedTextField(
                                        value = packageName,
                                        onValueChange = { packageName = it },
                                        label = { Text(if (isEnglish) "App Package Name" else "حزمة التطبيق (Package Name)", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DefaultShortcutBlue,
                                            unfocusedBorderColor = Color(0xFF2F3443),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )

                                    OutlinedTextField(
                                        value = externalUrl,
                                        onValueChange = { externalUrl = it },
                                        label = { Text(if (isEnglish) "External Web URL (optional)" else "أو رابط خارجي (اختياري)", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DefaultShortcutBlue,
                                            unfocusedBorderColor = Color(0xFF2F3443),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )

                                    // Quick popular presets
                                    Text(
                                        text = if (isEnglish) "Quick Presets:" else "نماذج سريعة:",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 11.sp
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        PopularAppPresets.presets.take(8).forEach { preset ->
                                            FilterChip(
                                                selected = packageName == preset.packageName && preset.packageName.isNotBlank(),
                                                onClick = {
                                                    name = preset.displayLabel
                                                    customLabel = preset.displayLabel
                                                    packageName = preset.packageName
                                                    externalUrl = preset.externalUrl
                                                    selectedSymbol = preset.iconSymbol
                                                },
                                                label = { Text(preset.displayLabel, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    containerColor = Color(0xFF252834),
                                                    labelColor = Color(0xFFE5E7EB),
                                                    selectedContainerColor = DefaultShortcutBlue.copy(alpha = 0.35f)
                                                )
                                            )
                                        }
                                    }

                                    // Dual / Hidden switches
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Hide in Secret Vault" else "إخفاء في الخزنة السرية",
                                            color = Color(0xFFB8BDC9),
                                            fontSize = 12.sp
                                        )
                                        Switch(
                                            checked = isHidden,
                                            onCheckedChange = { isHidden = it },
                                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFEF4444))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ================= CARD 2: INLINE LABEL EDITOR CARD (Image 2 Card 2) =================
                    Surface(
                        color = EditorCardBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = if (isEnglish) "Edit Name" else "تعديل الاسم",
                                tint = Color(0xFFB8BDC9),
                                modifier = Modifier.size(21.dp)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                if (customLabel.isEmpty()) {
                                    Text(
                                        text = if (isEnglish) "Shortcut name..." else "اكتب اسم الاختصار...",
                                        color = Color(0xFF6B7280),
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = customLabel,
                                    onValueChange = { customLabel = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = Color(0xFFF3F4F6),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(DefaultShortcutBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("editor_label_input")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ================= CARD 3: ICON STYLE & SYMBOL 4-COLUMN GRID CARD (Image 2 Card 3) =================
                    Surface(
                        color = EditorCardBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { iconCardExpanded = !iconCardExpanded }
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = if (isEnglish) "Icon Style" else "شكل الأيقونة",
                                        tint = Color(0xFFB8BDC9),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = if (isEnglish) "Icon Style & Color" else "تخصيص الأيقونة واللون",
                                        color = Color(0xFFD1D5DB),
                                        fontSize = 13.sp
                                    )
                                }

                                Icon(
                                    imageVector = if (iconCardExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFFB8BDC9),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            AnimatedVisibility(visible = iconCardExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 18.dp),
                                    verticalArrangement = Arrangement.spacedBy(18.dp)
                                ) {
                                    // 4-Column Frame Style Grid (exact 4x2 layout from Image 2!)
                                    val styleRows = ShortcutFrameStyles.chunked(4)
                                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                                        styleRows.forEach { rowStyles ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                rowStyles.forEach { styleKey ->
                                                    val isSel = selectedStyle == styleKey
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier
                                                            .size(52.dp)
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .background(
                                                                if (isSel) Color(selectedColor).copy(alpha = 0.25f)
                                                                else Color.Transparent
                                                            )
                                                            .border(
                                                                width = if (isSel) 1.5.dp else 0.dp,
                                                                color = if (isSel) Color(selectedColor) else Color.Transparent,
                                                                shape = RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable { selectedStyle = styleKey }
                                                    ) {
                                                        ShortcutStyledGlyphContent(
                                                            iconSymbol = selectedSymbol,
                                                            iconStyle = styleKey,
                                                            accentColor = Color(selectedColor),
                                                            glyphSize = 36.dp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Geometric Symbol Picker Row (strictly geometric/abstract/object icons)
                                    Text(
                                        text = if (isEnglish) "Symbol:" else "الرمز الهندسي:",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 11.sp
                                    )
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ShortcutSymbolsPalette.forEach { sym ->
                                            val isSel = selectedSymbol == sym
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSel) Color(selectedColor)
                                                        else Color(0xFF252834)
                                                    )
                                                    .clickable { selectedSymbol = sym }
                                            ) {
                                                ShortcutSymbolRender(
                                                    symbol = sym,
                                                    tint = Color.White,
                                                    size = 20.dp
                                                )
                                            }
                                        }
                                    }

                                    // Color Swatches Row
                                    Text(
                                        text = if (isEnglish) "Circle Color:" else "لون الخلفية:",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 11.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ShortcutColorPalette.forEach { col ->
                                            val isSel = selectedColor == col
                                            Box(
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(col))
                                                    .border(
                                                        width = if (isSel) 2.5.dp else 0.dp,
                                                        color = if (isSel) Color.White else Color.Transparent,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { selectedColor = col }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showInstalledAppsInEditor) {
        InstalledAppsPickerDialog(
            viewModel = viewModel,
            isEnglish = isEnglish,
            useArabicNumerals = useArabicNumerals,
            onDismiss = { showInstalledAppsInEditor = false },
            onAppPicked = { picked ->
                name = picked.name
                customLabel = picked.name
                packageName = picked.packageName
                externalUrl = ""
                showInstalledAppsInEditor = false
            }
        )
    }
}

@Composable
private fun InstalledAppsPickerDialog(
    viewModel: MainViewModel,
    isEnglish: Boolean,
    useArabicNumerals: Boolean,
    onDismiss: () -> Unit,
    onAppPicked: (AppShortcut) -> Unit
) {
    val installedApps = remember { viewModel.getInstalledApps() }
    var appSearchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(installedApps, appSearchQuery) {
        if (appSearchQuery.isBlank()) installedApps
        else installedApps.filter {
            it.name.contains(appSearchQuery, ignoreCase = true) ||
                it.packageName.contains(appSearchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EditorCardBg,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = DefaultShortcutBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Installed Apps (${NumeralFormatter.formatNumber(installedApps.size, useArabicNumerals)})"
                        else "تطبيقات الهاتف (${NumeralFormatter.formatNumber(installedApps.size, useArabicNumerals)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = if (isEnglish) "Select any installed app to create a shortcut"
                    else "اختر أي تطبيق مثبت على هاتفك لإنشاء اختصار مباشر",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = appSearchQuery,
                    onValueChange = { appSearchQuery = it },
                    placeholder = { Text(if (isEnglish) "Search app name or package..." else "بحث باسم التطبيق أو الحزمة...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (appSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { appSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "No matching apps found" else "لم يتم العثور على تطبيقات مطابقة",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceVariant,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAppPicked(app) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = app.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = app.packageName,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = if (isEnglish) "+ Pick" else "+ اختيار",
                                        color = PrimaryCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEnglish) "Close" else "إغلاق", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ShortcutStyledCircleAvatar(
    iconColor: Color,
    iconSymbol: String,
    iconStyle: String = "BARE_LARGE",
    size: Dp = 56.dp
) {
    Surface(
        shape = CircleShape,
        color = iconColor,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            ShortcutStyledGlyphContent(
                iconSymbol = iconSymbol,
                iconStyle = iconStyle,
                accentColor = iconColor,
                glyphSize = size * 0.62f
            )
        }
    }
}

@Composable
fun ShortcutStyledGlyphContent(
    iconSymbol: String,
    iconStyle: String,
    accentColor: Color,
    glyphSize: Dp
) {
    when (iconStyle) {
        "WHITE_BOX" -> {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.size(glyphSize)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ShortcutSymbolRender(
                        symbol = iconSymbol,
                        tint = Color(0xFF1F2937),
                        size = glyphSize * 0.68f
                    )
                }
            }
        }
        "BARE_SMALL" -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(glyphSize)
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.65f
                )
            }
        }
        "SOLID_WHITE" -> {
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.size(glyphSize)
            ) {}
        }
        "OUTLINE_ROUNDED" -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(glyphSize)
                    .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.66f
                )
            }
        }
        "THICK_ROUNDED" -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(glyphSize)
                    .border(2.5.dp, Color.White, RoundedCornerShape(9.dp))
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.64f
                )
            }
        }
        "DOUBLE_ROUNDED" -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(glyphSize)
                    .border(2.dp, Color.White, RoundedCornerShape(9.dp))
                    .padding(3.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.56f
                )
            }
        }
        "CIRCLE_BADGE" -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(glyphSize)
                    .border(1.8.dp, Color.White, CircleShape)
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.64f
                )
            }
        }
        else -> {
            // BARE_LARGE
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(glyphSize)
            ) {
                ShortcutSymbolRender(
                    symbol = iconSymbol,
                    tint = Color.White,
                    size = glyphSize * 0.82f
                )
            }
        }
    }
}

@Composable
fun ShortcutSymbolRender(
    symbol: String,
    tint: Color,
    size: Dp
) {
    if (symbol == "Ayah") {
        // Ornate geometric 8-point Rub el Hizb Islamic star medallion (zero living beings)
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.minDimension
            val stroke = Stroke(width = (s * 0.085f).coerceAtLeast(1.4f))
            val side = s * 0.64f
            val topLeft = Offset((s - side) / 2f, (s - side) / 2f)
            val corner = CornerRadius(s * 0.06f, s * 0.06f)
            drawRoundRect(
                color = tint,
                topLeft = topLeft,
                size = Size(side, side),
                cornerRadius = corner,
                style = stroke
            )
            rotate(degrees = 45f) {
                drawRoundRect(
                    color = tint,
                    topLeft = topLeft,
                    size = Size(side, side),
                    cornerRadius = corner,
                    style = stroke
                )
            }
            drawCircle(
                color = tint,
                radius = s * 0.22f,
                style = stroke
            )
        }
    } else {
        Icon(
            imageVector = getIconVector(symbol),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
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
        border = BorderStroke(1.5.dp, iconColor),
        modifier = Modifier.size(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            ShortcutSymbolRender(
                symbol = shortcut.iconSymbol,
                tint = iconColor,
                size = 24.dp
            )
        }
    }
}

fun getIconVector(symbol: String): ImageVector = when (symbol) {
    "Settings" -> Icons.Default.Settings
    "Calendar" -> Icons.Default.CalendarToday
    "Tools" -> Icons.Default.Build
    "PlayArrow" -> Icons.Default.PlayArrow
    "Tiles" -> Icons.Default.Widgets
    "Clock" -> Icons.Default.Alarm
    "Translate" -> Icons.Default.Language
    "Phone" -> Icons.Default.Call
    "Chat" -> Icons.Default.Chat
    "Camera" -> Icons.Default.CameraAlt
    "Browser" -> Icons.Default.Public
    "Music" -> Icons.Default.MusicNote
    "Notes" -> Icons.Default.Note
    "Bookmark" -> Icons.Default.Bookmark
    "Shield" -> Icons.Default.Shield
    else -> Icons.Default.Apps
}
