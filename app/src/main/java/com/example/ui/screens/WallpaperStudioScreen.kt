package com.example.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.AiImageWidgetAnalyzer
import com.example.ai.AiWallpaperAnalysis
import com.example.data.model.WallpaperConfig
import com.example.data.model.WallpaperType
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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
    BackHandler {
        viewModel.navigateBack()
    }

    val currentWallpaper by viewModel.wallpaper.collectAsState()

    var selectedType by remember { mutableStateOf(currentWallpaper.type) }
    var selectedPreset by remember { mutableIntStateOf(currentWallpaper.presetIndex) }
    var customUri by remember { mutableStateOf(currentWallpaper.customImageUri) }
    var dimLevel by remember { mutableFloatStateOf(currentWallpaper.dimLevel) }
    var aiAnalysis by remember { mutableStateOf<AiWallpaperAnalysis?>(null) }

    // Modern Zero-Permission Photo Picker with AI Analysis
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customUri = uri.toString()
            selectedType = WallpaperType.EXTERNAL_IMAGE
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    val analysis = AiImageWidgetAnalyzer.analyzeAndGenerateWallpaper(bitmap, uri.toString())
                    aiAnalysis = analysis
                    dimLevel = analysis.dimLevel
                    viewModel.showToast("تم تحليل الصورة وضبط التباين الذكي للويدجات (${(analysis.dimLevel * 100).toInt()}%) 🪄")
                }
            } catch (e: Exception) {
                // fallback
            }
        }
    }

    // Fallback file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customUri = uri.toString()
            selectedType = WallpaperType.EXTERNAL_IMAGE
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
                            text = "استوديو الخلفيات والتصميم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "تعديل الخلفيات وتوليدها بالذكاء الاصطناعي أوفلاين",
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
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = DarkBackground
                        )
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
            // Live Simulated Wallpaper Preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
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

                        // Preview box with dimming layer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
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
                                    contentDescription = "Wallpaper Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Dimming layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = dimLevel))
                            )

                            // Sample mockup elements inside
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
                                        text = if (selectedType == WallpaperType.EXTERNAL_IMAGE) "خلفية خارجية مستوردة 🖼️" else presetGradients[selectedPreset].first,
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
                                    Surface(
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(38.dp)
                                    ) {}
                                    Surface(
                                        color = PrimaryCyan.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(38.dp)
                                    ) {}
                                    Surface(
                                        color = Color(0xFFA855F7).copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(38.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }

            // External Image Import & AI Analysis Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "إضافة صورة وتوليد خلفية بالذكاء الاصطناعي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryCyan
                        )

                        Text(
                            text = "اختر أي صورة ليقوم الذكاء الاصطناعي بحساب نسبة التعتيم المثالية والتباين أوفلاين لحماية العين وجعل الويدجات واضحة.",
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
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_pick_photo_wallpaper")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = DarkBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("معرض الصور", color = DarkBackground, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    filePickerLauncher.launch("image/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ملفات الهاتف", color = TextPrimary)
                            }
                        }

                        if (aiAnalysis != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFF8B5CF6),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = aiAnalysis!!.styleTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFC084FC)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = aiAnalysis!!.styleDescription,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dimming / Darkness Slider
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مستوى تعتيم الخلفية (Dim Level)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${(dimLevel * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
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
                Text(
                    text = "أو اختر من التدرجات الفنية المدمجة:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(presetGradients.size) { index ->
                val preset = presetGradients[index]
                val isSelected = selectedType == WallpaperType.PRESET_GRADIENT && selectedPreset == index

                Surface(
                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryCyan else DarkBorder
                    ),
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
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PrimaryCyan
                            )
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
