package com.example.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.AiImageWidgetAnalyzer
import com.example.ai.AiWallpaperAnalysis
import com.example.data.ai.PlaceholderWallpaperAiRepository
import com.example.data.ai.WallpaperAiRepository
import com.example.data.ai.wallpaperAiPresets
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.io.File

val presetGradients = listOf(
    "سايبر نيون ديب" to listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0284C7)),
    "سديم الفضاء الكوني" to listOf(Color(0xFF180A2A), Color(0xFF2E1065), Color(0xFF4C1D95)),
    "شفق الغروب الساحلي" to listOf(Color(0xFF431407), Color(0xFF7C2D12), Color(0xFFEA580C)),
    "زمرد الأورورا القطبي" to listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF059669)),
    "أسود داكن OLED فائق" to listOf(Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFF141414)),
    "الموج الأزرق الزجاجي" to listOf(Color(0xFF082F49), Color(0xFF0369A1), Color(0xFF38BDF8))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val wallpaperAiRepo: WallpaperAiRepository = remember { PlaceholderWallpaperAiRepository() }

    BackHandler {
        viewModel.navigateBack()
    }

    val currentWallpaper by viewModel.wallpaper.collectAsState()

    var selectedType by remember { mutableStateOf(currentWallpaper.type) }
    var selectedPreset by remember { mutableIntStateOf(currentWallpaper.presetIndex) }
    var customUri by remember { mutableStateOf(currentWallpaper.customImageUri) }
    var dimLevel by remember { mutableFloatStateOf(currentWallpaper.dimLevel) }
    var aiAnalysis by remember { mutableStateOf<AiWallpaperAnalysis?>(null) }

    var aiPrompt by remember { mutableStateOf("") }
    var selectedAiStyleId by remember { mutableStateOf(wallpaperAiPresets.first().id) }
    var isGeneratingAi by remember { mutableStateOf(false) }

    fun applyImportedWallpaperUri(uri: Uri) {
        customUri = uri.toString()
        selectedType = WallpaperType.EXTERNAL_IMAGE
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    val analysis = AiImageWidgetAnalyzer.analyzeAndGenerateWallpaper(bitmap, uri.toString())
                    aiAnalysis = analysis
                    dimLevel = analysis.dimLevel
                    viewModel.showToast("تم استيراد الصورة وضبط التباين الذكي (${(analysis.dimLevel * 100).toInt()}%)")
                }
            }
        }
    }

    // Modern Photo Picker (Gallery)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) applyImportedWallpaperUri(uri)
    }

    // File Manager Picker (OpenDocument with persistable URI permission)
    val fileManagerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            applyImportedWallpaperUri(uri)
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
                            text = "قسم الخلفيات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "إضافة من مدير الملفات وتوليد بالذكاء الاصطناعي",
                            fontSize = 11.sp,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_wallpaper")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val newConfig = WallpaperConfig(
                                type = selectedType,
                                presetIndex = selectedPreset,
                                customImageUri = customUri,
                                dimLevel = dimLevel
                            )
                            viewModel.updateWallpaper(newConfig)
                            viewModel.navigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_wallpaper")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = DarkBackground)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تطبيق الخلفية وحفظ التعديلات",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
            // Live Wallpaper Preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "معاينة الخلفية الحية:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (selectedType == WallpaperType.EXTERNAL_IMAGE && customUri != null) {
                                        Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                    } else {
                                        val gradientColors = presetGradients.getOrElse(selectedPreset) { presetGradients[0] }.second
                                        Brush.verticalGradient(gradientColors)
                                    }
                                )
                        ) {
                            if (selectedType == WallpaperType.EXTERNAL_IMAGE && customUri != null) {
                                AsyncImage(
                                    model = customUri,
                                    contentDescription = "معاينة الخلفية",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = dimLevel))
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (selectedType == WallpaperType.EXTERNAL_IMAGE) {
                                            "خلفية مخصصة / مولدة"
                                        } else {
                                            presetGradients[selectedPreset].first
                                        },
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Surface(color = Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(38.dp)) {}
                                    Surface(color = PrimaryCyan.copy(alpha = 0.35f), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(38.dp)) {}
                                    Surface(color = Color(0xFFA855F7).copy(alpha = 0.35f), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(38.dp)) {}
                                }
                            }
                        }
                    }
                }
            }

            // Pick from File Manager or Gallery
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "إضافة خلفية من مدير الملفات أو المعرض",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryCyan
                        )

                        Text(
                            text = "اختر أي صورة من مدير الملفات في هاتفك أو من معرض الصور لتعيينها كخلفية رئيسية مع ضبط التباين التلقائي.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { fileManagerLauncher.launch(arrayOf("image/*")) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_pick_file_manager_wallpaper")
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = DarkBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مدير الملفات", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_pick_photo_wallpaper")
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("معرض الصور", color = TextPrimary, fontSize = 12.sp)
                            }
                        }

                        if (aiAnalysis != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(aiAnalysis!!.styleTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC084FC))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(aiAnalysis!!.styleDescription, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                }
            }

            // AI Wallpaper Generator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B).copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "توليد الخلفيات بالذكاء الاصطناعي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }

                        // Notice when Firebase is not enabled
                        Surface(
                            color = AccentAmber.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = wallpaperAiRepo.statusNotice(),
                                    color = Color(0xFFFDE68A),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = aiPrompt,
                            onValueChange = { aiPrompt = it },
                            placeholder = { Text("صف الخلفية المطلوبة (مثال: زخرفة هندسية ذهبية، سديم سماوي)...", fontSize = 12.sp) },
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFC084FC),
                                unfocusedBorderColor = Color(0xFF4C1D95),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wallpaper_ai_prompt_input")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            wallpaperAiPresets.chunked(3).forEach { rowPresets ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowPresets.forEach { preset ->
                                        val isPicked = selectedAiStyleId == preset.id
                                        Surface(
                                            onClick = { selectedAiStyleId = preset.id },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isPicked) Color(0xFFA855F7).copy(alpha = 0.35f) else Color(0xFFA855F7).copy(alpha = 0.14f),
                                            border = BorderStroke(1.dp, if (isPicked) Color(0xFFC084FC) else Color(0xFFA855F7).copy(alpha = 0.35f)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = preset.titleAr,
                                                fontSize = 10.sp,
                                                color = Color(0xFFE9D5FF),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val style = wallpaperAiPresets.firstOrNull { it.id == selectedAiStyleId } ?: wallpaperAiPresets.first()
                                scope.launch {
                                    isGeneratingAi = true
                                    val result = wallpaperAiRepo.generateWallpaper(context, aiPrompt, style)
                                    customUri = Uri.fromFile(File(result.imageFilePath)).toString()
                                    selectedType = WallpaperType.EXTERNAL_IMAGE
                                    dimLevel = result.recommendedDim
                                    isGeneratingAi = false
                                    viewModel.showToast("تم توليد خلفية (${result.styleTitle}) بنجاح")
                                }
                            },
                            enabled = !isGeneratingAi,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_generate_ai_wallpaper")
                        ) {
                            if (isGeneratingAi) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري توليد الخلفية...", color = Color.White, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("توليد خلفية بالذكاء الاصطناعي", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Dimming Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مستوى تعتيم الخلفية (Dim Level)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("${(dimLevel * 100).toInt()}%", fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        }

                        Slider(
                            value = dimLevel,
                            onValueChange = { dimLevel = it },
                            valueRange = 0f..0.8f,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryCyan,
                                activeTrackColor = PrimaryCyan,
                                inactiveTrackColor = DarkBorder
                            )
                        )
                    }
                }
            }

            // Built-in Gradient Presets
            item {
                Text("أو اختر من التدرجات الفنية المدمجة:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(presetGradients.size) { index ->
                val preset = presetGradients[index]
                val isSelected = selectedType == WallpaperType.PRESET_GRADIENT && selectedPreset == index

                Surface(
                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) PrimaryCyan else DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedType = WallpaperType.PRESET_GRADIENT
                            selectedPreset = index
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(preset.second))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = preset.first,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) PrimaryCyan else TextPrimary
                            )
                        }

                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PrimaryCyan)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
