package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppShortcut
import com.example.data.model.IconShape
import com.example.data.model.PopularAppPresets
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter
import com.example.util.formatNumerals

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val shortcuts by viewModel.shortcuts.collectAsState()
    val notificationShortcutsEnabled by viewModel.notificationShortcutsEnabled.collectAsState()
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()

    // State dialogs
    var selectedShortcutForAction by remember { mutableStateOf<AppShortcut?>(null) }
    var showAddShortcutDialog by remember { mutableStateOf(false) }
    var showInstalledAppsPicker by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showNavigationDrawerSheet by remember { mutableStateOf(false) }
    var showEditShortcutDialog by remember { mutableStateOf<AppShortcut?>(null) }

    // Active vs Inactive shortcuts
    val activeShortcuts = remember(shortcuts) {
        shortcuts.filter { it.isActive && !it.isHidden }.sortedBy { it.layerIndex }
    }
    val inactiveShortcuts = remember(shortcuts) {
        shortcuts.filter { !it.isActive && !it.isHidden }
    }

    Scaffold(
        containerColor = Color(0xFF13151D),
        // Red coral FAB at bottom center matching the screenshot
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddShortcutDialog = true },
                shape = CircleShape,
                containerColor = Color(0xFFE11D48),
                contentColor = Color.White,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة اختصار",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
        // Bottom bar replacing top bar per user request: "مع تحويل شريط العلوي إلى شريط السفلي"
        bottomBar = {
            Surface(
                color = Color(0xFF1A1C26),
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3344).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right group: Hamburger Menu for all app studios & Help
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showNavigationDrawerSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "القائمة والأقسام",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        IconButton(
                            onClick = { showHelpDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "المساعدة والتعليمات",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Center: 1234 ⇄ ١٢٣٤ Numeral Switcher Button
                    Surface(
                        onClick = { viewModel.toggleArabicNumerals() },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF262938),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = if (useArabicNumerals) "١٢٣٤" else "1234",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "تبديل الأرقام",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (useArabicNumerals) "1234" else "١٢٣٤",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Left group: Tile Mode Icon (Red) and Notification Center Elements (Blue)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Blue Notification Shortcuts Toggle Icon
                        Surface(
                            onClick = {
                                viewModel.toggleNotificationShortcuts(!notificationShortcutsEnabled)
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (notificationShortcutsEnabled) Color(0xFF2563EB) else Color(0xFF1E293B),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (notificationShortcutsEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "عناصر شريط الإشعارات",
                                    tint = if (notificationShortcutsEnabled) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Red Tile Shortcuts Mode Icon (matching screenshot)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(5.dp).background(Color.White, CircleShape))
                                    Box(modifier = Modifier.size(5.dp).background(Color.White, CircleShape))
                                    Box(modifier = Modifier.size(5.dp).background(Color.White, CircleShape))
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: الاختصارات النشطة
            item(span = { GridItemSpan(4) }) {
                Text(
                    text = "الاختصارات النشطه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )
            }

            // Grid items for Active Shortcuts (كما في الصورة)
            items(activeShortcuts, key = { it.id }) { shortcut ->
                ShortcutItemTile(
                    shortcut = shortcut,
                    useArabicNumerals = useArabicNumerals,
                    isActiveSection = true,
                    onClick = { viewModel.launchAppShortcut(shortcut) },
                    onLongClick = { selectedShortcutForAction = shortcut }
                )
            }

            // Divider between Active and Inactive sections
            item(span = { GridItemSpan(4) }) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(
                    color = Color(0xFF262938),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "الاختصارات الغير نشطة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 6.dp)
                )
            }

            // Grid items for Inactive Shortcuts (كما في الصورة)
            items(inactiveShortcuts, key = { it.id }) { shortcut ->
                ShortcutItemTile(
                    shortcut = shortcut,
                    useArabicNumerals = useArabicNumerals,
                    isActiveSection = false,
                    onClick = { selectedShortcutForAction = shortcut },
                    onLongClick = { selectedShortcutForAction = shortcut }
                )
            }

            // Empty state if no inactive shortcuts
            if (inactiveShortcuts.isEmpty()) {
                item(span = { GridItemSpan(4) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد اختصارات غير نشطة حالياً",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // ---------------- SHORTCUT ACTION SHEET ----------------
    if (selectedShortcutForAction != null) {
        val s = selectedShortcutForAction!!
        ModalBottomSheet(
            onDismissRequest = { selectedShortcutForAction = null },
            containerColor = Color(0xFF1E202B),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular icon preview
                Surface(
                    shape = CircleShape,
                    color = Color(s.iconColor),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ShortcutIconSymbol(s.iconSymbol, Color.White, 28)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = s.displayLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
                Text(
                    text = if (s.isActive) s.getLayerLabel(useArabicNumerals) else "غير نشط حالياً",
                    fontSize = 12.sp,
                    color = if (s.isActive) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions list
                Button(
                    onClick = {
                        selectedShortcutForAction = null
                        viewModel.launchAppShortcut(s)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تشغيل التطبيق الآن 🚀", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle active/inactive
                Button(
                    onClick = {
                        val id = s.id
                        selectedShortcutForAction = null
                        viewModel.toggleShortcutActive(id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (s.isActive) Color(0xFF334155) else Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (s.isActive) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (s.isActive) "نقل إلى الاختصارات الغير نشطة" else "تفعيل ونقل إلى الاختصارات النشطة ⚡",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Edit shortcut
                Button(
                    onClick = {
                        showEditShortcutDialog = s
                        selectedShortcutForAction = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تعديل الاسم والطبقة والرمز", color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delete shortcut
                Button(
                    onClick = {
                        val id = s.id
                        selectedShortcutForAction = null
                        viewModel.deleteShortcut(id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFF87171))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف الاختصار نهائياً", color = Color(0xFFF87171))
                }
            }
        }
    }

    // ---------------- ADD SHORTCUT DIALOG ----------------
    if (showAddShortcutDialog) {
        ModalBottomSheet(
            onDismissRequest = { showAddShortcutDialog = false },
            containerColor = Color(0xFF1E202B),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "إضافة اختصار جديد ⚡",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Pick installed apps
                Card(
                    onClick = {
                        showAddShortcutDialog = false
                        showInstalledAppsPicker = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2563EB),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Apps, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📱 استعراض تطبيقات الهاتف (بحث فوري)",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "اختر من أي تطبيق مثبت على هاتفك لإضافته مباشرة في طبقات الإشعارات",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Custom package / external URL
                Card(
                    onClick = {
                        showAddShortcutDialog = false
                        showEditShortcutDialog = AppShortcut(
                            name = "تطبيق جديد",
                            customLabel = "تطبيق مخصص",
                            packageName = "",
                            iconColor = 0xFF0284C7,
                            isActive = true
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🌐 إضافة رابط موقع خارجي أو حزمة APK مخصصة",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "فتح روابط الويب الخارجية أو تشغيل حزم تطبيقات الطرف الثالث مباشرة",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "نماذج سريعة شائعة:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PopularAppPresets.presets.take(4).forEach { preset ->
                        Surface(
                            onClick = {
                                viewModel.addShortcut(preset.copy(isActive = true))
                                showAddShortcutDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(preset.iconColor).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(preset.iconColor).copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ShortcutIconSymbol(preset.iconSymbol, Color(preset.iconColor), 20)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.displayLabel,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------- INSTALLED APPS PICKER DIALOG ----------------
    if (showInstalledAppsPicker) {
        val installedApps = remember { viewModel.getInstalledApps() }
        var searchQuery by remember { mutableStateOf("") }
        val filtered = remember(searchQuery, installedApps) {
            if (searchQuery.isBlank()) installedApps
            else installedApps.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }

        AlertDialog(
            onDismissRequest = { showInstalledAppsPicker = false },
            containerColor = Color(0xFF1E202B),
            title = {
                Text(
                    text = "اختر تطبيقاً من الهاتف (${NumeralFormatter.formatNumber(installedApps.size, useArabicNumerals)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث باسم التطبيق أو الحزمة...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.height(300.dp)) {
                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered) { app ->
                                Card(
                                    onClick = {
                                        viewModel.addShortcut(app.copy(isActive = true))
                                        showInstalledAppsPicker = false
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(app.iconColor),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(imageVector = Icons.Default.Apps, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = app.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text(text = app.packageName, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInstalledAppsPicker = false }) {
                    Text("إلغاء", color = PrimaryCyan)
                }
            }
        )
    }

    // ---------------- EDIT SHORTCUT DIALOG ----------------
    if (showEditShortcutDialog != null) {
        val editing = showEditShortcutDialog!!
        var label by remember { mutableStateOf(editing.displayLabel) }
        var pkg by remember { mutableStateOf(editing.packageName) }
        var url by remember { mutableStateOf(editing.externalUrl) }
        var layerText by remember { mutableStateOf(editing.layerIndex.toString()) }

        AlertDialog(
            onDismissRequest = { showEditShortcutDialog = null },
            containerColor = Color(0xFF1E202B),
            title = {
                Text(
                    text = "تعديل تفاصيل الاختصار",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("اسم الاختصار الظاهر") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                    )

                    OutlinedTextField(
                        value = layerText,
                        onValueChange = { layerText = it },
                        label = { Text("رقم الطبقة (Layer Index)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                    )

                    OutlinedTextField(
                        value = pkg,
                        onValueChange = { pkg = it },
                        label = { Text("اسم حزمة التطبيق (Package Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("رابط خارجي (Web URL - اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCyan)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedLayer = layerText.toIntOrNull() ?: editing.layerIndex
                        val updated = editing.copy(
                            customLabel = label.trim(),
                            packageName = pkg.trim(),
                            externalUrl = url.trim(),
                            layerIndex = parsedLayer
                        )
                        viewModel.updateShortcut(updated)
                        showEditShortcutDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditShortcutDialog = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }

    // ---------------- HELP DIALOG ----------------
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = Color(0xFF1E202B),
            title = {
                Text(
                    text = "دليل استخدام اختصارات المربعات والإشعارات 💡",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "• الاختصارات النشطة: تظهر بترتيب أرقام الطبقات (طبقة 1، طبقة 2...) وهي التي تظهر مباشرة في مركز إشعارات أندرويد وتتيح الوصول السريع بلمسة واحدة.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• تفعيل شريط الإشعارات: اضغط على الأيقونة الزرقاء في الشريط السفلي لتشغيل شريط الاختصارات الدائم في مركز إشعارات الهاتف.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• تبديل الأرقام: اضغط على زر (1234 ⇄ ١٢٣٤) في الشريط السفلي للتحويل بين الأرقام الإنجليزية والعربية في كامل التطبيق.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• إدارة أي اختصار: اضغط مطولاً على أي اختصار لتعديل طبقته، أو نقله إلى غير النشطة، أو حذفه.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("حسناً فهمت", color = PrimaryCyan)
                }
            }
        )
    }

    // ---------------- NAVIGATION DRAWER SHEET ----------------
    if (showNavigationDrawerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNavigationDrawerSheet = false },
            containerColor = Color(0xFF1A1C26),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "أقسام واستوديوهات التطبيق 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                // 1. All Widgets Studio
                DrawerNavigationTile(
                    title = "استوديو ومحرر الويدجات (${NumeralFormatter.formatNumber(viewModel.allWidgets.collectAsState().value.size, useArabicNumerals)})",
                    subtitle = "تخصيص وتصميم ويدجات الشاشة الرئيسية",
                    icon = Icons.Default.Widgets,
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        showNavigationDrawerSheet = false
                        viewModel.navigateTo(AppScreen.AllWidgets)
                    }
                )

                // 2. AI Image to Widget
                DrawerNavigationTile(
                    title = "تحويل الصور إلى ويدجت بالذكاء 🪄",
                    subtitle = "مطابقة صور الشاشات واستخراج الألوان محلياً",
                    icon = Icons.Default.AutoAwesome,
                    tint = Color(0xFFA855F7),
                    onClick = {
                        showNavigationDrawerSheet = false
                        viewModel.navigateTo(AppScreen.AiImageWidgetStudio)
                    }
                )

                // 3. Keyboard & Wallpaper Studio
                DrawerNavigationTile(
                    title = "استوديو لوحة المفاتيح والخلفيات ⌨️",
                    subtitle = "سمات كيبورد تفاعلية وخلفيات بدون نت",
                    icon = Icons.Default.Keyboard,
                    tint = Color(0xFF10B981),
                    onClick = {
                        showNavigationDrawerSheet = false
                        viewModel.navigateTo(AppScreen.KeyboardStudio)
                    }
                )

                // 4. Alerts & Reminders Studio
                DrawerNavigationTile(
                    title = "استوديو التنبيهات والمواعيد ⏰",
                    subtitle = "منبهات صلاة، تذكيرات مخصصة بنغمات",
                    icon = Icons.Default.Alarm,
                    tint = Color(0xFFEAB308),
                    onClick = {
                        showNavigationDrawerSheet = false
                        viewModel.navigateTo(AppScreen.AlertsStudio)
                    }
                )

                // 5. Hidden Apps Vault
                DrawerNavigationTile(
                    title = "خزنة التطبيقات المخفية 🔒",
                    subtitle = "حماية الخصوصية برمز مرور محلي",
                    icon = Icons.Default.Lock,
                    tint = Color(0xFFF43F5E),
                    onClick = {
                        showNavigationDrawerSheet = false
                        viewModel.navigateTo(AppScreen.HiddenAppsVault)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ---------------- SHORTCUT TILE COMPOSABLE (MATCHING SCREENSHOT) ----------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortcutItemTile(
    shortcut: AppShortcut,
    useArabicNumerals: Boolean,
    isActiveSection: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 4.dp)
    ) {
        // Round Circular App Icon matching screenshot
        Surface(
            shape = CircleShape,
            color = Color(shortcut.iconColor),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                ShortcutIconSymbol(
                    symbol = shortcut.iconSymbol,
                    tint = Color.White,
                    sizeDp = 26
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // App Name Label
        Text(
            text = shortcut.displayLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFF1F5F9),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Subtitle: (طبقة 1) / (طبقة ١) in active, or status in inactive
        if (isActiveSection) {
            Text(
                text = shortcut.getLayerLabel(useArabicNumerals),
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ---------------- SHORTCUT ICON SYMBOL PICKER ----------------
@Composable
private fun ShortcutIconSymbol(symbol: String, tint: Color, sizeDp: Int) {
    val vector: ImageVector = when (symbol) {
        "Settings" -> Icons.Default.Settings
        "Calendar" -> Icons.Default.ViewAgenda
        "Tools" -> Icons.Default.Apps
        "PlayArrow" -> Icons.Default.PlayArrow
        "Tiles" -> Icons.Default.Widgets
        "Clock" -> Icons.Default.Alarm
        "Translate" -> Icons.Default.Language
        "Chat" -> Icons.Default.Apps
        "Phone" -> Icons.Default.Call
        "Camera" -> Icons.Default.CameraAlt
        else -> Icons.Default.Apps
    }
    Icon(
        imageVector = vector,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(sizeDp.dp)
    )
}

// ---------------- DRAWER NAVIGATION TILE ----------------
@Composable
private fun DrawerNavigationTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = tint.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}
