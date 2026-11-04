package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.model.OfflineDataProvider
import com.example.data.model.WidgetConfig
import com.example.ui.components.OfflineStatusBadge
import com.example.ui.components.WidgetPreviewCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WidgetConfigureActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set result to CANCELED first so if user cancels, widget isn't placed
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MyApplicationTheme {
                WidgetConfigureScreen(
                    onWidgetSelected = { selectedConfig ->
                        applyWidgetAndFinish(selectedConfig)
                    },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun applyWidgetAndFinish(config: WidgetConfig) {
        val context = this
        val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
        scope.launch {
            val db = AppDatabase.getInstance(context)
            db.widgetDao().clearActiveLauncherWidgets()
            db.widgetDao().setActiveLauncherWidget(config.id)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = CustomAppWidgetProvider.buildRemoteViews(context, config)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            withContext(Dispatchers.Main) {
                val resultValue = Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                setResult(RESULT_OK, resultValue)
                finish()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigureScreen(
    onWidgetSelected: (WidgetConfig) -> Unit,
    onCancel: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var widgetList by remember { mutableStateOf<List<WidgetConfig>>(emptyList()) }
    var selectedWidget by remember { mutableStateOf<WidgetConfig?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val saved = db.widgetDao().getAllWidgetsFlow()
            val list = db.widgetDao().getCount()
            if (list == 0) {
                widgetList = OfflineDataProvider.preinstalledTemplates
            } else {
                // Read all
                val entities = db.widgetDao().getActiveLauncherWidget()
            }
        }
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.IO) {
            db.widgetDao().getAllWidgetsFlow().collect { entities ->
                val configs = if (entities.isEmpty()) {
                    OfflineDataProvider.preinstalledTemplates
                } else {
                    entities.map { it.toDomain() }
                }
                withContext(Dispatchers.Main) {
                    widgetList = configs
                    if (selectedWidget == null && configs.isNotEmpty()) {
                        selectedWidget = configs.first()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "إضافة ويدجت للشاشة الرئيسية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "اختر الويدجت المطلوب إضافته بدون ملفات",
                            fontSize = 11.sp,
                            color = PrimaryCyan
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
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            selectedWidget?.let { onWidgetSelected(it) }
                        },
                        enabled = selectedWidget != null,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("btn_confirm_add_home_widget")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = DarkBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة للشاشة الرئيسية",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Selected Live Preview
            if (selectedWidget != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "معاينة الويدجت المحدد:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                            WidgetPreviewCard(config = selectedWidget!!, isInteractive = false)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "اختر من ويدجاتك وقوالبك:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(widgetList, key = { it.id }) { widget ->
                val isSelected = selectedWidget?.id == widget.id
                Surface(
                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryCyan else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedWidget = widget }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = widget.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) PrimaryCyan else TextPrimary
                            )
                            Text(
                                text = "${widget.type.titleAr} • ${widget.size.cells}",
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
