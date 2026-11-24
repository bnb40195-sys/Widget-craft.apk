package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.browser.BrowserLanguage
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumeralFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val useArabicNumerals by viewModel.useArabicNumerals.collectAsState()
    val is24HourFormat by viewModel.is24HourFormat.collectAsState()
    val wallpaper by viewModel.wallpaper.collectAsState()
    val notificationShortcutsEnabled by viewModel.notificationShortcutsEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isEn = appLanguage == BrowserLanguage.ENGLISH

    var dimLevel by remember { mutableFloatStateOf(wallpaper.dimLevel) }
    var blurRadius by remember { mutableFloatStateOf(wallpaper.blurRadius) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setExternalWallpaperUri(uri)
        }
    }

    BackHandler {
        viewModel.navigateBack()
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
    ) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isEn) "App Settings ⚙️" else "إعدادات التطبيق ⚙️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isEn) "Language, numerals, appearance and wallpapers" else "تخصيص اللغة، تنسيق الأرقام، المظهر والخلفيات",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isEn) "Back" else "رجوع",
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
            // Section 1: اللغة (Language)
            item {
                SettingsSectionCard(
                    title = if (isEn) "Language & Translation" else "اللغة والترجمة (Language)",
                    icon = Icons.Default.Language,
                    iconTint = Color(0xFF38BDF8)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LanguageOptionPill(
                            title = "العربية (Arabic)",
                            badge = if (isEn) "Arabic 🇸🇦" else "افتراضي 🇸🇦",
                            isSelected = appLanguage == BrowserLanguage.ARABIC,
                            onClick = { viewModel.setAppLanguage(BrowserLanguage.ARABIC) },
                            modifier = Modifier.weight(1f)
                        )
                        LanguageOptionPill(
                            title = "English",
                            badge = "Global 🌐",
                            isSelected = appLanguage == BrowserLanguage.ENGLISH,
                            onClick = { viewModel.setAppLanguage(BrowserLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 2: الأرقام وتنسيق الساعة (Numerals & Clock Format)
            item {
                SettingsSectionCard(
                    title = if (isEn) "Numerals & Clock Format" else "تنسيق الأرقام والساعة",
                    icon = Icons.Default.Dialpad,
                    iconTint = Color(0xFFF59E0B)
                ) {
                    // Arabic vs Western Numerals Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEn) "App Numerals Format:" else "صيغة الأرقام في كامل التطبيق:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (useArabicNumerals) {
                                    if (isEn) "Eastern Arabic (١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠)" else "الأرقام العربية (١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠)"
                                } else {
                                    if (isEn) "Western Numerals (1 2 3 4 5 6 7 8 9 0)" else "الأرقام الإنجليزية (1 2 3 4 5 6 7 8 9 0)"
                                },
                                fontSize = 11.sp,
                                color = if (useArabicNumerals) Color(0xFF38BDF8) else TextSecondary
                            )
                        }

                        Surface(
                            onClick = { viewModel.toggleArabicNumerals() },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF262938),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (useArabicNumerals) "١٢٣٤" else "1234",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (useArabicNumerals) "1234" else "١٢٣٤",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 12-Hour vs 24-Hour Format Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEn) "Clock Display Format:" else "نظام عرض التوقيت والساعة:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (is24HourFormat) {
                                    if (isEn) "24-hour format (00:00 - 23:59)" else "نظام 24 ساعة (00:00 - 23:59)"
                                } else {
                                    if (isEn) "12-hour format (AM / PM)" else "نظام 12 ساعة (ص / م)"
                                },
                                fontSize = 11.sp,
                                color = if (is24HourFormat) Color(0xFF10B981) else TextSecondary
                            )
                        }

                        Switch(
                            checked = is24HourFormat,
                            onCheckedChange = { viewModel.toggle24HourFormat(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }
            }

            // Section 3: الخلفيات والمظهر (Wallpapers & Appearance)
            item {
                SettingsSectionCard(
                    title = if (isEn) "Wallpapers & Appearance" else "تخصيص الخلفيات والمظهر",
                    icon = Icons.Default.Wallpaper,
                    iconTint = Color(0xFFA855F7)
                ) {
                    Text(
                        text = if (isEn) "Choose wallpaper style or customize background:" else "اختر نمط الخلفية أو ولد خلفية فريدة بالذكاء:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.WallpaperStudio) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7).copy(alpha = 0.25f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEn) "Wallpaper Studio" else "توليد بالذكاء 🪄", color = Color(0xFFE9D5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB).copy(alpha = 0.25f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEn) "Pick Photo" else "رفع صورة من الهاتف", color = Color(0xFFBAE6FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dim level slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isEn) "Background Dim Level:" else "مستوى تعتيم الخلفية:", fontSize = 11.sp, color = TextSecondary)
                        Text("${(dimLevel * 100).toInt()}%", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = dimLevel,
                        onValueChange = {
                            dimLevel = it
                            viewModel.updateWallpaper(wallpaper.copy(dimLevel = it))
                        },
                        valueRange = 0f..0.8f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF2563EB))
                    )
                }
            }

            // Section 4: شريط الإشعارات والمربعات
            item {
                SettingsSectionCard(
                    title = if (isEn) "Notification Shortcuts & Tiles" else "اختصارات الإشعارات والمربعات",
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFF2563EB)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEn) "Persistent Notification Bar:" else "تفعيل عناصر مركز الإشعارات الدائم:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (notificationShortcutsEnabled) {
                                    if (isEn) "Active — launch pinned apps in 1 tap" else "نشط ويعرض التطبيقات بلمسة واحدة"
                                } else {
                                    if (isEn) "Currently paused" else "متوقف حالياً"
                                },
                                fontSize = 11.sp,
                                color = if (notificationShortcutsEnabled) Color(0xFF10B981) else TextSecondary
                            )
                        }

                        Switch(
                            checked = notificationShortcutsEnabled,
                            onCheckedChange = { viewModel.toggleNotificationShortcuts(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB)
                            )
                        )
                    }
                }
            }

            // Section 5: حول التطبيق والأمان
            item {
                SettingsSectionCard(
                    title = if (isEn) "About & Privacy" else "حول التطبيق والأمان",
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF10B981)
                ) {
                    Text(
                        text = if (isEn) "Themes Studio - v1.0.0" else "استوديو ثيمات - الإصدار 1.0.0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isEn) "Built with high privacy and geometric-only visuals." else "مصمم بخصوصية عالية، وخالي من أي صور لذوات الأرواح طبقاً للضوابط الشرعية.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun LanguageOptionPill(
    title: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.25f) else Color(0xFF13151D),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF38BDF8) else TextPrimary
                )
                Text(text = badge, fontSize = 10.sp, color = TextSecondary)
            }
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
            }
        }
    }
}
