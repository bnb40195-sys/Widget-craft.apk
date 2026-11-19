package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.AiImageWidgetStudioScreen
import com.example.ui.screens.AlertsStudioScreen
import com.example.ui.screens.HiddenAppsVaultScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KeyboardStudioScreen
import com.example.ui.screens.MediaDownloaderScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.AllWidgetsScreen
import com.example.ui.screens.ShortcutsStudioScreen
import com.example.ui.screens.WallpaperStudioScreen
import com.example.ui.screens.WidgetEditorScreen
import com.example.ui.screens.WidgetImportScreen
import com.example.ui.screens.InAppBrowserScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState is AppScreen.InAppBrowser || initialState is AppScreen.InAppBrowser) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                fadeIn() togetherWith fadeOut()
                            }
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is AppScreen.Home -> HomeScreen(viewModel = viewModel)
                            is AppScreen.AllWidgets -> AllWidgetsScreen(viewModel = viewModel)
                            is AppScreen.Editor -> WidgetEditorScreen(
                                viewModel = viewModel,
                                widgetId = screen.widgetId,
                                baseTemplate = screen.baseTemplate
                            )
                            is AppScreen.Import -> WidgetImportScreen(viewModel = viewModel)
                            is AppScreen.MediaDownloader -> MediaDownloaderScreen(viewModel = viewModel)
                            is AppScreen.VideoPlayer -> VideoPlayerScreen(
                                viewModel = viewModel,
                                initialVideoUri = screen.videoUri,
                                initialVideoTitle = screen.videoTitle
                            )
                            is AppScreen.Settings -> SettingsScreen(viewModel = viewModel)
                            is AppScreen.WallpaperStudio -> WallpaperStudioScreen(viewModel = viewModel)
                            is AppScreen.ShortcutsStudio -> ShortcutsStudioScreen(viewModel = viewModel)
                            is AppScreen.KeyboardStudio -> KeyboardStudioScreen(viewModel = viewModel)
                            is AppScreen.HiddenAppsVault -> HiddenAppsVaultScreen(viewModel = viewModel)
                            is AppScreen.AlertsStudio -> AlertsStudioScreen(viewModel = viewModel)
                            is AppScreen.AiImageWidgetStudio -> AiImageWidgetStudioScreen(viewModel = viewModel)
                            is AppScreen.InAppBrowser -> InAppBrowserScreen(viewModel = viewModel, initialUrl = screen.url)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        // 1. Check if opened via launcher widget tap
        val widgetId = intent.getStringExtra("EXTRA_WIDGET_ID")
        if (widgetId != null) {
            viewModel.navigateTo(AppScreen.Editor(widgetId = widgetId))
            return
        }

        // 2. Check if opened via SEND intent (shared text/json from outside)
        if (intent.action == Intent.ACTION_SEND) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.navigateTo(AppScreen.Import)
            }
        }

        // 3. Check if opened via VIEW intent (opening a .json or .widget file from file manager)
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri != null) {
                try {
                    val stream = contentResolver.openInputStream(uri)
                    val content = stream?.bufferedReader()?.use { it.readText() }
                    if (!content.isNullOrBlank()) {
                        viewModel.importWidgetFromJson(content) { imported ->
                            viewModel.navigateTo(AppScreen.Editor(widgetId = imported.id))
                        }
                    }
                } catch (e: Exception) {
                    viewModel.showToast("تعذر استيراد الملف الخارجي")
                }
            }
        }
    }
}
