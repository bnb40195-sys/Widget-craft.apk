package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.AiImageWidgetAnalyzer
import com.example.ai.AiKeyboardAnalysis
import com.example.data.model.KeyboardTheme
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    BackHandler {
        viewModel.navigateBack()
    }

    val activeTheme by viewModel.activeKeyboardTheme.collectAsState()
    val allThemes by viewModel.keyboardThemes.collectAsState()

    var testInputText by remember { mutableStateOf("") }
    var importJsonText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Keyboard, 1: AI Generator, 2: Import JSON

    // AI Generation State
    var aiSelectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var aiAnalysisResult by remember { mutableStateOf<AiKeyboardAnalysis?>(null) }
    var aiUseImageAsBackground by remember { mutableStateOf(true) }

    // Modern Photo Picker for AI Keyboard
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            aiSelectedImageUri = uri
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    val analysis = AiImageWidgetAnalyzer.analyzeAndGenerateKeyboard(
                        bitmap = bitmap,
                        imageUri = if (aiUseImageAsBackground) uri.toString() else null
                    )
                    aiAnalysisResult = analysis
                    viewModel.showToast("تم تحليل الصورة وتوليد لوحة المفاتيح بالذكاء الاصطناعي 🪄")
                }
            } catch (e: Exception) {
                viewModel.showToast("تعذر قراءة الصورة")
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val content = stream?.bufferedReader()?.use { it.readText() }
                if (!content.isNullOrBlank()) {
                    viewModel.importKeyboardThemeFromJson(content)
                }
            } catch (e: Exception) {
                viewModel.showToast("تعذر قراءة ملف السمة")
            }
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
                            text = "لوحة المفاتيح والسمات الذكية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "تشغيل وتوليد لوحة المفاتيح بالذكاء الاصطناعي",
                            fontSize = 11.sp,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_keyboard")
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceVariant,
                contentColor = PrimaryCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("تجربة وتشغيل ⌨️", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("توليد بالذكاء 🪄", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("استيراد JSON 📥", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Interactive Live Keyboard & Theme Selector
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Live Interactive Typing Board
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Keyboard,
                                                contentDescription = null,
                                                tint = PrimaryCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "شاشة الكتابة المباشرة (${activeTheme.name})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryCyan
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (testInputText.isNotEmpty()) {
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("TypedText", testInputText))
                                                        viewModel.showToast("تم نسخ النص إلى الحافظة! 📋")
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                                }

                                                IconButton(
                                                    onClick = { testInputText = "" },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }

                                    Surface(
                                        color = DarkBackground,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = testInputText.ifEmpty { "المس المفاتيح بالأسفل للكتابة والتجربة..." },
                                                color = if (testInputText.isEmpty()) TextSecondary else TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // The Interactive Keyboard View
                        item {
                            InteractiveKeyboardWidget(
                                theme = activeTheme,
                                onKeyPressed = { char ->
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    testInputText += char
                                },
                                onBackspace = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (testInputText.isNotEmpty()) {
                                        testInputText = testInputText.dropLast(1)
                                    }
                                },
                                onSpace = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    testInputText += " "
                                },
                                onEnter = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    testInputText += "\n"
                                }
                            )
                        }

                        // CTA To AI Generator
                        item {
                            Card(
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.12f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "هل تريد صنع لوحة مفاتيح من صورتك؟ 🪄",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "ارفع أي صورة ليقوم الذكاء الاصطناعي بمطابقة ألوانها وتثبيتها كلوحة مفاتيح حية.",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Available Themes List
                        item {
                            Text(
                                text = "سمات لوحة المفاتيح المحفوظة (${allThemes.size}):",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        items(allThemes, key = { it.id }) { theme ->
                            val isSelected = activeTheme.id == theme.id
                            Surface(
                                color = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurface,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PrimaryCyan else DarkBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectKeyboardTheme(theme) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(theme.backgroundColor))
                                                .border(1.dp, Color(theme.accentColor), RoundedCornerShape(8.dp))
                                        ) {
                                            if (theme.backgroundImageUri != null) {
                                                AsyncImage(
                                                    model = theme.backgroundImageUri,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = theme.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) PrimaryCyan else TextPrimary
                                            )
                                            Text(
                                                text = if (theme.backgroundImageUri != null) "سمة ذكاء اصطناعي مخصصة 🪄" else if (theme.isCustomImported) "سمة مخصصة" else "سمة مدمجة",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PrimaryCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: AI Keyboard Generator from Image (100% Offline)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = Color(0xFF8B5CF6),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "توليد لوحة المفاتيح بالذكاء الاصطناعي",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "ارفع أي صورة ليقوم الذكاء بمطابقة تصميمها وتشغيلها مباشرة",
                                                fontSize = 11.sp,
                                                color = Color(0xFFC084FC)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "اختر صورة من هاتفك: سيقوم الذكاء الاصطناعي بتحليل الألوان بدقة، حساب تباين المفاتيح للقراءة المريحة، وتوليد سمة لوحة مفاتيح تفاعلية جاهزة للكتابة فوراً.",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 17.sp
                                    )

                                    // Pick Image Button
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (aiSelectedImageUri == null) "اختيار صورة لتحليلها وتوليد اللوحة 🖼️" else "اختيار صورة أخرى 🔄",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Background option toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "تطبيق مظهر الصورة على مفاتيح اللوحة",
                                            fontSize = 12.sp,
                                            color = TextPrimary
                                        )
                                        Switch(
                                            checked = aiUseImageAsBackground,
                                            onCheckedChange = { checked ->
                                                aiUseImageAsBackground = checked
                                                if (aiSelectedImageUri != null) {
                                                    try {
                                                        val stream = context.contentResolver.openInputStream(aiSelectedImageUri!!)
                                                        val bitmap = BitmapFactory.decodeStream(stream)
                                                        if (bitmap != null) {
                                                            aiAnalysisResult = AiImageWidgetAnalyzer.analyzeAndGenerateKeyboard(
                                                                bitmap = bitmap,
                                                                imageUri = if (checked) aiSelectedImageUri.toString() else null
                                                            )
                                                        }
                                                    } catch (e: Exception) {
                                                        // ignore
                                                    }
                                                }
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = DarkBackground,
                                                checkedTrackColor = Color(0xFF8B5CF6)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Analysis Result Preview & Apply
                        if (aiAnalysisResult != null) {
                            val analysis = aiAnalysisResult!!
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
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
                                            Text(
                                                text = "🎨 نتيجة التحليل: ${analysis.styleTitle}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = PrimaryCyan
                                            )
                                            Surface(
                                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "مطابق للسمة",
                                                    color = Color(0xFF34D399),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = analysis.styleDescription,
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            lineHeight = 16.sp
                                        )

                                        // Extracted Palette Chips
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            PaletteChip(label = "الإطار", hex = analysis.dominantHex, color = Color(analysis.theme.backgroundColor), modifier = Modifier.weight(1f))
                                            PaletteChip(label = "المفاتيح", hex = analysis.keyHex, color = Color(analysis.theme.keyColor), modifier = Modifier.weight(1f))
                                            PaletteChip(label = "الحروف", hex = analysis.textHex, color = Color(analysis.theme.textColor), modifier = Modifier.weight(1f))
                                            PaletteChip(label = "النيون", hex = analysis.accentHex, color = Color(analysis.theme.accentColor), modifier = Modifier.weight(1f))
                                        }

                                        // Mini Preview
                                        Text(
                                            text = "معاينة لوحة المفاتيح المولدة:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )

                                        InteractiveKeyboardWidget(
                                            theme = analysis.theme,
                                            onKeyPressed = { char ->
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                testInputText += char
                                            },
                                            onBackspace = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                if (testInputText.isNotEmpty()) testInputText = testInputText.dropLast(1)
                                            },
                                            onSpace = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                testInputText += " "
                                            },
                                            onEnter = {}
                                        )

                                        // Apply and Run Button
                                        Button(
                                            onClick = {
                                                viewModel.applyAiGeneratedKeyboardTheme(analysis.theme)
                                                selectedTab = 0 // switch to interactive live keyboard
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .testTag("btn_apply_ai_keyboard")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "تشغيل وتطبيق كلوحة مفاتيح نشطة فوراً 🚀",
                                                color = DarkBackground,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Import Theme JSON
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "إضافة سمة لوحة مفاتيح من خارج التطبيق",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryCyan
                                    )

                                    Text(
                                        text = "يمكنك استيراد سمات وتخطيطات مخصصة بصيغة JSON من أصدقائك أو مصممين خارجيين وتطبيقها فوراً.",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = clipboard.primaryClip
                                                if (clip != null && clip.itemCount > 0) {
                                                    importJsonText = clip.getItemAt(0).text?.toString() ?: ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.ContentPaste, contentDescription = null, tint = PrimaryCyan)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("لصق الكود", color = PrimaryCyan, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { filePickerLauncher.launch("*/*") },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.FileOpen, contentDescription = null, tint = TextPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("اختيار ملف", color = TextPrimary, fontSize = 12.sp)
                                        }
                                    }

                                    OutlinedTextField(
                                        value = importJsonText,
                                        onValueChange = { importJsonText = it },
                                        label = { Text("كود JSON لسمة لوحة المفاتيح") },
                                        placeholder = { Text("{\n  \"name\": \"سمة النيون الذهبي\",\n  ...\n}") },
                                        minLines = 4,
                                        maxLines = 8,
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = TextPrimary
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.importKeyboardThemeFromJson(importJsonText)
                                            selectedTab = 0
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("btn_confirm_import_kb")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, tint = DarkBackground)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("استيراد وتفعيل السمة", color = DarkBackground, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveKeyboardWidget(
    theme: KeyboardTheme,
    onKeyPressed: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit = {}
) {
    val bgColor = Color(theme.backgroundColor)
    val keyColor = Color(theme.keyColor).copy(alpha = theme.keyAlpha)
    val textColor = Color(theme.textColor)
    val accentColor = Color(theme.accentColor)
    val radius = theme.keyCornerRadiusDp.dp

    var isArabicLayout by remember { mutableStateOf(true) }
    var isSymbolsMode by remember { mutableStateOf(false) }

    val arabicRow1 = listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠")
    val arabicRow2 = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج")
    val arabicRow3 = listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط")
    val arabicRow4 = listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د")

    val englishRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val englishRow2 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    val englishRow3 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    val englishRow4 = listOf("Z", "X", "C", "V", "B", "N", "M")

    val symbolsRow1 = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")")
    val symbolsRow2 = listOf("+", "-", "=", "_", "[", "]", "{", "}", "\\", "|")
    val symbolsRow3 = listOf(";", ":", "'", "\"", "<", ">", ",", ".", "?", "/")
    val symbolsRow4 = listOf("~", "`", "€", "£", "¥", "§", "°", "«", "»", "✨")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
    ) {
        // Optional Background Image for AI Theme
        if (theme.backgroundImageUri != null) {
            AsyncImage(
                model = theme.backgroundImageUri,
                contentDescription = "Keyboard Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
            )
            // Tint overlay for readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
        }

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val rows = when {
                isSymbolsMode -> listOf(symbolsRow1, symbolsRow2, symbolsRow3, symbolsRow4)
                isArabicLayout -> listOf(arabicRow1, arabicRow2, arabicRow3, arabicRow4)
                else -> listOf(englishRow1, englishRow2, englishRow3, englishRow4)
            }

            rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    rowKeys.forEach { key ->
                        Surface(
                            color = keyColor,
                            shape = RoundedCornerShape(radius),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.keyBorderColor)),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clickable { onKeyPressed(key) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Control Row: Language / Symbols / Spacebar / Backspace / Enter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Symbols / ABC toggle
                Surface(
                    color = keyColor,
                    shape = RoundedCornerShape(radius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.keyBorderColor)),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .clickable { isSymbolsMode = !isSymbolsMode }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isSymbolsMode) "أب/AB" else "?123",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Language toggle
                Surface(
                    color = keyColor,
                    shape = RoundedCornerShape(radius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.keyBorderColor)),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(40.dp)
                        .clickable {
                            isArabicLayout = !isArabicLayout
                            isSymbolsMode = false
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isArabicLayout) "عربي" else "EN",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Spacebar
                Surface(
                    color = accentColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(radius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor),
                    modifier = Modifier
                        .weight(3.5f)
                        .height(40.dp)
                        .clickable { onSpace() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isArabicLayout) "مسافة" else "Space",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Backspace
                Surface(
                    color = keyColor,
                    shape = RoundedCornerShape(radius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(theme.keyBorderColor)),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .clickable { onBackspace() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Enter key
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(radius),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .clickable { onEnter() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "↵",
                            color = DarkBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteChip(label: String, hex: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = DarkBackground,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            )
            Text(text = label, fontSize = 9.sp, color = TextSecondary)
            Text(text = hex, fontSize = 8.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
        }
    }
}
