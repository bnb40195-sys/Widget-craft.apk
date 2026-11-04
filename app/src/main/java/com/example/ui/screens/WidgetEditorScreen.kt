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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfflineDataProvider
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetTheme
import com.example.data.model.WidgetType
import com.example.ui.MainViewModel
import com.example.ui.components.SizeSelectorRow
import com.example.ui.components.ThemeSelectorRow
import com.example.ui.components.WidgetPreviewCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun WidgetEditorScreen(
    viewModel: MainViewModel,
    widgetId: String?,
    baseTemplate: WidgetConfig?,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val allWidgets by viewModel.allWidgets.collectAsState()

    // Determine initial state
    var editingConfig by remember {
        mutableStateOf(
            baseTemplate ?: allWidgets.find { it.id == widgetId } ?: WidgetConfig(
                name = "ويدجت جديد مخصص",
                tagline = "ويدجت صممته بنفسي بدون نت"
            )
        )
    }

    // Tab state: 0 -> النوع والحجم, 1 -> المظهر والألوان, 2 -> المحتوى والتفاصيل
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (widgetId != null) "تعديل الويدجت" else "صنع ويدجت جديد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = editingConfig.name,
                            fontSize = 11.sp,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.copyWidgetJsonToClipboard(editingConfig) },
                        modifier = Modifier.testTag("btn_export_code")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Export Code",
                            tint = TextSecondary
                        )
                    }

                    Button(
                        onClick = { viewModel.saveWidget(editingConfig) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("btn_save_widget")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "حفظ",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
            // Live Interactive Preview Section
            Surface(
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المعاينة الحية الفورية (Live Preview)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )

                        Text(
                            text = editingConfig.size.cells,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    WidgetPreviewCard(
                        config = editingConfig,
                        isInteractive = true,
                        onCounterIncrement = {
                            editingConfig = editingConfig.copy(
                                counterValue = editingConfig.counterValue + editingConfig.counterStep
                            )
                        }
                    )
                }
            }

            // Editor Tabs
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
                    text = { Text("النوع والحجم", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("المظهر والألوان", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("المحتوى والبيانات", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            // Scrollable Settings Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // TAB 0: Type and Size
                        item {
                            EditorCard(title = "نوع وتصميم الويدجت (Widget Type)") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    WidgetType.values().forEach { type ->
                                        val isSelected = editingConfig.type == type
                                        Surface(
                                            color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) PrimaryCyan else DarkBorder
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    editingConfig = editingConfig.copy(type = type)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = type.titleAr,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) PrimaryCyan else TextPrimary,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = type.titleEn,
                                                        fontSize = 11.sp,
                                                        color = TextSecondary
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
                                }
                            }
                        }

                        item {
                            EditorCard(title = "أبعاد وحجم الويدجت (Widget Size)") {
                                SizeSelectorRow(
                                    selectedSize = editingConfig.size,
                                    onSizeSelect = { editingConfig = editingConfig.copy(size = it) }
                                )
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: Theme, Colors, Radius
                        item {
                            EditorCard(title = "حزمة الألوان والتدرج اللوني (Theme Preset)") {
                                ThemeSelectorRow(
                                    selectedTheme = editingConfig.theme,
                                    onThemeSelect = { theme ->
                                        editingConfig = editingConfig.copy(
                                            theme = theme,
                                            customPrimaryColor = null,
                                            customSecondaryColor = null,
                                            customTextColor = null
                                        )
                                    }
                                )
                            }
                        }

                        item {
                            EditorCard(title = "انحناء الزوايا: ${editingConfig.cornerRadiusDp} dp") {
                                Column {
                                    Slider(
                                        value = editingConfig.cornerRadiusDp.toFloat(),
                                        onValueChange = {
                                            editingConfig = editingConfig.copy(cornerRadiusDp = it.toInt())
                                        },
                                        valueRange = 8f..36f,
                                        steps = 14,
                                        colors = SliderDefaults.colors(
                                            thumbColor = PrimaryCyan,
                                            activeTrackColor = PrimaryCyan,
                                            inactiveTrackColor = DarkBorder
                                        ),
                                        modifier = Modifier.testTag("slider_corner_radius")
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("حاد (8dp)", fontSize = 11.sp, color = TextSecondary)
                                        Text("دائري جداً (36dp)", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }

                        item {
                            EditorCard(title = "ألوان مخصصة سريعة") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val quickColors = listOf(
                                        0xFF0F172A to 0xFF1E293B,
                                        0xFF1E1B4B to 0xFF4338CA,
                                        0xFF064E3B to 0xFF059669,
                                        0xFF701A75 to 0xFFD946EF,
                                        0xFF7C2D12 to 0xFFEA580C
                                    )
                                    quickColors.forEach { (c1, c2) ->
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(c1),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(c2)),
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clickable {
                                                    editingConfig = editingConfig.copy(
                                                        customPrimaryColor = c1,
                                                        customSecondaryColor = c2
                                                    )
                                                }
                                        ) {}
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // TAB 2: Content & Elements
                        item {
                            EditorCard(title = "العناوين والمعلومات الأساسية") {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = editingConfig.name,
                                        onValueChange = { editingConfig = editingConfig.copy(name = it) },
                                        label = { Text("اسم الويدجت") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = editorTextFieldColors()
                                    )

                                    OutlinedTextField(
                                        value = editingConfig.tagline,
                                        onValueChange = { editingConfig = editingConfig.copy(tagline = it) },
                                        label = { Text("وصف مختصر / شعار") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = editorTextFieldColors()
                                    )
                                }
                            }
                        }

                        // Specific content based on Widget Type
                        when (editingConfig.type) {
                            WidgetType.DIGITAL_CLOCK -> {
                                item {
                                    EditorCard(title = "عناصر الساعة الرقمية") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("إظهار التاريخ الكامل", color = TextPrimary, fontSize = 14.sp)
                                                Switch(
                                                    checked = editingConfig.showDate,
                                                    onCheckedChange = { editingConfig = editingConfig.copy(showDate = it) },
                                                    colors = editorSwitchColors()
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("إظهار مؤشر بطارية الهاتف", color = TextPrimary, fontSize = 14.sp)
                                                Switch(
                                                    checked = editingConfig.showBattery,
                                                    onCheckedChange = { editingConfig = editingConfig.copy(showBattery = it) },
                                                    colors = editorSwitchColors()
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("إظهار الثواني الحية", color = TextPrimary, fontSize = 14.sp)
                                                Switch(
                                                    checked = editingConfig.showSeconds,
                                                    onCheckedChange = { editingConfig = editingConfig.copy(showSeconds = it) },
                                                    colors = editorSwitchColors()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.QUICK_NOTES, WidgetType.MINIMAL_STAT -> {
                                item {
                                    EditorCard(title = "نص الملاحظة والمفكرة") {
                                        OutlinedTextField(
                                            value = editingConfig.noteContent,
                                            onValueChange = { editingConfig = editingConfig.copy(noteContent = it) },
                                            label = { Text("محتوى الملاحظة") },
                                            minLines = 3,
                                            maxLines = 6,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = editorTextFieldColors()
                                        )
                                    }
                                }
                            }

                            WidgetType.HABIT_COUNTER -> {
                                item {
                                    EditorCard(title = "إعدادات عداد التسبيح والإنجاز") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OutlinedTextField(
                                                value = editingConfig.counterLabel,
                                                onValueChange = { editingConfig = editingConfig.copy(counterLabel = it) },
                                                label = { Text("تسمية العداد (مثال: سبحان الله)") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = editorTextFieldColors()
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = editingConfig.counterValue.toString(),
                                                    onValueChange = {
                                                        editingConfig = editingConfig.copy(counterValue = it.toIntOrNull() ?: 0)
                                                    },
                                                    label = { Text("القيمة الحالية") },
                                                    singleLine = true,
                                                    modifier = Modifier.weight(1f),
                                                    colors = editorTextFieldColors()
                                                )

                                                OutlinedTextField(
                                                    value = editingConfig.counterStep.toString(),
                                                    onValueChange = {
                                                        editingConfig = editingConfig.copy(counterStep = it.toIntOrNull() ?: 1)
                                                    },
                                                    label = { Text("قيمة الزيادة بكل ضغطة") },
                                                    singleLine = true,
                                                    modifier = Modifier.weight(1f),
                                                    colors = editorTextFieldColors()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.DAILY_QUOTE -> {
                                item {
                                    EditorCard(title = "نص الحكمة والعبارة") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OutlinedTextField(
                                                value = editingConfig.quoteArabic,
                                                onValueChange = { editingConfig = editingConfig.copy(quoteArabic = it) },
                                                label = { Text("العبارة بالعربية") },
                                                minLines = 2,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = editorTextFieldColors()
                                            )

                                            OutlinedTextField(
                                                value = editingConfig.quoteAuthor,
                                                onValueChange = { editingConfig = editingConfig.copy(quoteAuthor = it) },
                                                label = { Text("القائل / المصدر") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = editorTextFieldColors()
                                            )

                                            Button(
                                                onClick = {
                                                    val randomQ = OfflineDataProvider.getRandomQuote()
                                                    editingConfig = editingConfig.copy(
                                                        quoteArabic = randomQ.arabic,
                                                        quoteEnglish = randomQ.english,
                                                        quoteAuthor = randomQ.author
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    tint = PrimaryCyan
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("جلب حكمة عشوائية من المكتبة بدون نت", color = TextPrimary)
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.ANALOG_CLOCK -> {
                                item {
                                    EditorCard(title = "إعدادات ساعة العقارب (Analog Clock)") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "نمط وطراز الساعة",
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            val styles = listOf(
                                                "alg_dark" to "طراز وهران ALG الداكن (مثل الصورة)",
                                                "classic" to "طراز كلاسيكي فاخر",
                                                "minimal" to "طراز مينيمل عصري"
                                            )
                                            styles.forEach { (styleKey, styleName) ->
                                                val isSelected = editingConfig.analogStyle == styleKey
                                                Surface(
                                                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) PrimaryCyan else DarkBorder
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            editingConfig = editingConfig.copy(analogStyle = styleKey)
                                                        }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(10.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(styleName, color = if (isSelected) PrimaryCyan else TextPrimary, fontSize = 13.sp)
                                                        if (isSelected) {
                                                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("إظهار عقرب الثواني المتحرك", color = TextPrimary, fontSize = 14.sp)
                                                Switch(
                                                    checked = editingConfig.showSeconds,
                                                    onCheckedChange = { editingConfig = editingConfig.copy(showSeconds = it) },
                                                    colors = editorSwitchColors()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.PRAYER_TIMES, WidgetType.ISLAMIC_CALENDAR_CARD -> {
                                item {
                                    EditorCard(title = "إعدادات المدينة ومواقيت الصلاة الفلكية (100% بدون نت)") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "المدينة المختارة: ${editingConfig.cityName}",
                                                color = PrimaryCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )

                                            Text(
                                                text = "اختر مدينة لحساب الأوقات فلكياً بدون إنترنت:",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )

                                            // City chips
                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                com.example.device.IslamicTimeCalculator.popularCities.forEach { city ->
                                                    val isSelected = editingConfig.cityName == city.name
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = {
                                                            editingConfig = editingConfig.copy(
                                                                cityName = city.name,
                                                                cityLatitude = city.latitude,
                                                                cityLongitude = city.longitude,
                                                                cityTimeZone = city.timeZoneOffsetHours
                                                            )
                                                        },
                                                        label = { Text(city.name, fontSize = 11.sp) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = PrimaryCyan,
                                                            selectedLabelColor = DarkBackground,
                                                            containerColor = DarkSurfaceVariant,
                                                            labelColor = TextPrimary
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "تعديل التقويم الهجري (تعديل الرؤية): ${if (editingConfig.hijriDayOffset > 0) "+${editingConfig.hijriDayOffset}" else editingConfig.hijriDayOffset} يوم",
                                                color = TextPrimary,
                                                fontSize = 13.sp
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf(-2, -1, 0, 1, 2).forEach { offset ->
                                                    Button(
                                                        onClick = { editingConfig = editingConfig.copy(hijriDayOffset = offset) },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (editingConfig.hijriDayOffset == offset) PrimaryCyan else DarkSurfaceVariant
                                                        ),
                                                        modifier = Modifier.weight(1f),
                                                        contentPadding = PaddingValues(0.dp)
                                                    ) {
                                                        Text(
                                                            text = if (offset > 0) "+$offset" else "$offset",
                                                            color = if (editingConfig.hijriDayOffset == offset) DarkBackground else TextPrimary,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.WEATHER_TEMP -> {
                                item {
                                    EditorCard(title = "إعدادات الطقس والحرارة (بدون نت)") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OutlinedTextField(
                                                value = editingConfig.cityName,
                                                onValueChange = { editingConfig = editingConfig.copy(cityName = it) },
                                                label = { Text("المدينة / الموقع") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = editorTextFieldColors()
                                            )

                                            Text(
                                                text = "درجة الحرارة: ${editingConfig.temperatureCelsius}°م",
                                                color = PrimaryCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )

                                            Slider(
                                                value = editingConfig.temperatureCelsius.toFloat(),
                                                onValueChange = { editingConfig = editingConfig.copy(temperatureCelsius = it.toInt()) },
                                                valueRange = -10f..50f,
                                                steps = 59,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = PrimaryCyan,
                                                    activeTrackColor = PrimaryCyan,
                                                    inactiveTrackColor = DarkBorder
                                                )
                                            )

                                            Text(
                                                text = "حالة الطقس:",
                                                color = TextPrimary,
                                                fontSize = 13.sp
                                            )

                                            val conditions = listOf(
                                                "مشمس ودافئ ☀️",
                                                "غائم جزئياً ⛅",
                                                "ماطر ولطيف 🌧️",
                                                "صافٍ ومعتدل 🌤️",
                                                "رياح نشطة 💨",
                                                "بارد ومثلج ❄️"
                                            )

                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                conditions.forEach { cond ->
                                                    val isSelected = editingConfig.weatherCondition == cond
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = { editingConfig = editingConfig.copy(weatherCondition = cond) },
                                                        label = { Text(cond, fontSize = 11.sp) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = PrimaryCyan,
                                                            selectedLabelColor = DarkBackground,
                                                            containerColor = DarkSurfaceVariant,
                                                            labelColor = TextPrimary
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            WidgetType.QURAN_AUDIO -> {
                                item {
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    val isAudioPlaying by com.example.audio.AudioPlaybackManager.isPlaying.collectAsState()

                                    EditorCard(title = "إعدادات ويدجت القرآن الكريم بصوت (بدون نت 100%)") {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "اختر السورة / الآية:",
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            com.example.audio.AudioPlaybackManager.availableSurahs.forEach { surah ->
                                                val isSelected = editingConfig.quranSurahName == surah.name
                                                Surface(
                                                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) PrimaryCyan else DarkBorder
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            editingConfig = editingConfig.copy(
                                                                quranSurahName = surah.name,
                                                                noteContent = surah.ayahText
                                                            )
                                                        }
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = surah.name,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSelected) PrimaryCyan else TextPrimary,
                                                                fontSize = 13.sp
                                                            )
                                                            if (isSelected) {
                                                                Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                                            }
                                                        }
                                                        Text(
                                                            text = surah.ayahText,
                                                            fontSize = 11.sp,
                                                            color = TextSecondary,
                                                            maxLines = 2
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "القارئ:",
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            val reciters = listOf(
                                                "مشاري راشد العفاسي",
                                                "عبد الباسط عبد الصمد",
                                                "ماهر المعيقلي",
                                                "سعد الغامدي"
                                            )
                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                reciters.forEach { reciter ->
                                                    val isSelected = editingConfig.quranReciterName == reciter
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = { editingConfig = editingConfig.copy(quranReciterName = reciter) },
                                                        label = { Text(reciter, fontSize = 11.sp) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = PrimaryCyan,
                                                            selectedLabelColor = DarkBackground,
                                                            containerColor = DarkSurfaceVariant,
                                                            labelColor = TextPrimary
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = {
                                                    val surah = com.example.audio.AudioPlaybackManager.availableSurahs.find { it.name == editingConfig.quranSurahName }
                                                        ?: com.example.audio.AudioPlaybackManager.availableSurahs.first()
                                                    com.example.audio.AudioPlaybackManager.playSurah(
                                                        context = context,
                                                        surah = surah,
                                                        reciterName = editingConfig.quranReciterName
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isAudioPlaying) Color(0xFF10B981) else PrimaryCyan
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(
                                                    text = if (isAudioPlaying) "⏹️ إيقاف التلاوة الحالية" else "▶️ تجربة الاستماع للتلاوة (بدون نت)",
                                                    color = DarkBackground,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            else -> {}
                        }

                        // Launcher Integration options
                        item {
                            EditorCard(title = "التثبيت على الشاشة الرئيسية") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("تعيين كويدجت رئيسي", fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text("يعرض على شاشة هاتفك تلقائياً", fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Switch(
                                            checked = editingConfig.isActiveOnLauncher,
                                            onCheckedChange = {
                                                editingConfig = editingConfig.copy(isActiveOnLauncher = it)
                                            },
                                            colors = editorSwitchColors()
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.pinToHomeScreen(editingConfig) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = null,
                                            tint = DarkBackground
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "إرسال طلب تثبيت للشاشة الرئيسية",
                                            color = DarkBackground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Final Save Button Item
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.saveWidget(editingConfig) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = DarkBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حفظ الويدجت في مكتبتي",
                            color = DarkBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan
            )
            content()
        }
    }
}

@Composable
private fun editorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSurfaceVariant,
    unfocusedContainerColor = DarkSurfaceVariant,
    focusedBorderColor = PrimaryCyan,
    unfocusedBorderColor = DarkBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = PrimaryCyan,
    unfocusedLabelColor = TextSecondary
)

@Composable
private fun editorSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = DarkBackground,
    checkedTrackColor = PrimaryCyan,
    uncheckedThumbColor = TextSecondary,
    uncheckedTrackColor = DarkBorder
)
