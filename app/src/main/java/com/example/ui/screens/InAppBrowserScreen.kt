package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.ai.BrowserAiRepository
import com.example.data.ai.BrowserAiResponse
import com.example.data.ai.PlaceholderBrowserAiRepository
import com.example.data.browser.BrowserLanguage
import com.example.data.browser.BrowserSearchEngine
import com.example.data.browser.BrowserSettingsConfig
import com.example.data.browser.BrowserStorageManager
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.browser.BrowserAiAssistantSheet
import com.example.ui.screens.browser.BrowserBookmarksSheet
import com.example.ui.screens.browser.BrowserDownloadsSheet
import com.example.ui.screens.browser.BrowserHistorySheet
import com.example.ui.screens.browser.BrowserSettingsSheet
import com.example.ui.screens.browser.BrowserToolsMenuSheet
import com.example.ui.screens.browser.BrowserTranslateSheet
import com.example.ui.screens.browser.browserStringsFor
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONTokener
import java.io.File
import java.util.Collections
import java.util.WeakHashMap
import kotlin.coroutines.resume

private fun isEmulatorEnvironment(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
        Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.startsWith("unknown") ||
        Build.HARDWARE.contains("goldfish") ||
        Build.HARDWARE.contains("ranchu") ||
        Build.MODEL.contains("google_sdk") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for") ||
        Build.MANUFACTURER.contains("Genymotion") ||
        Build.PRODUCT.contains("sdk") ||
        Build.PRODUCT.contains("emulator") ||
        Build.PRODUCT.contains("simulator")
}

private enum class BrowserActiveSheet {
    NONE,
    TOOLS,
    AI_ASSISTANT,
    TRANSLATE,
    BOOKMARKS,
    HISTORY,
    DOWNLOADS,
    SETTINGS
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppBrowserScreen(
    viewModel: MainViewModel,
    initialUrl: String = ""
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val storageManager = remember { BrowserStorageManager(context) }
    val aiRepository: BrowserAiRepository = remember { PlaceholderBrowserAiRepository() }

    var settingsState by remember { mutableStateOf(storageManager.loadSettings()) }
    val strings = remember(settingsState.language) { browserStringsFor(settingsState.language) }
    val browserLayoutDirection = if (settingsState.language == BrowserLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    var bookmarks by remember { mutableStateOf(storageManager.loadBookmarks()) }
    var historyItems by remember { mutableStateOf(storageManager.loadHistory()) }
    var downloads by remember { mutableStateOf(storageManager.loadDownloads()) }

    var showStartPage by remember { mutableStateOf(initialUrl.isBlank()) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var webViewGeneration by remember { mutableIntStateOf(0) }
    var pendingLocalHtml by remember { mutableStateOf<Pair<String, String>?>(null) }

    var currentUrl by remember { mutableStateOf(if (initialUrl.isBlank()) "about:blank" else initialUrl) }
    var inputUrlText by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf(strings.defaultWebTitle) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    var activeSheet by remember { mutableStateOf(BrowserActiveSheet.NONE) }
    var showFindInPageBar by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }

    var aiBusy by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf<BrowserAiResponse?>(null) }
    var translateLanguage by remember { mutableStateOf("العربية") }
    var translateResult by remember { mutableStateOf<BrowserAiResponse?>(null) }

    val isCurrentBookmarked = remember(bookmarks, currentUrl, showStartPage) {
        !showStartPage && bookmarks.any { it.url.equals(currentUrl, ignoreCase = true) }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val destroyedWebViews = remember { Collections.newSetFromMap(WeakHashMap<WebView, Boolean>()) }
    var lastCrashTimestamp by remember { mutableStateOf(0L) }

    fun safelyDestroyWebView(wv: WebView?) {
        if (wv == null || !destroyedWebViews.add(wv)) return
        try {
            wv.stopLoading()
            wv.onPause()
            wv.webChromeClient = null
            wv.webViewClient = WebViewClient()
            wv.setDownloadListener(null)
            wv.removeAllViews()
            (wv.parent as? ViewGroup)?.removeView(wv)
            wv.destroy()
        } catch (_: Exception) {
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webViewRef?.onPause()
                Lifecycle.Event.ON_RESUME -> webViewRef?.onResume()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(webViewGeneration) {
        onDispose {
            safelyDestroyWebView(webViewRef)
            webViewRef = null
        }
    }

    fun applyWebViewSettings(wv: WebView, config: BrowserSettingsConfig) {
        wv.settings.apply {
            javaScriptEnabled = config.javaScriptEnabled
            domStorageEnabled = !config.privateMode
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            textZoom = config.textZoomPercent
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = !config.blockPopups
            allowFileAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            cacheMode = if (config.privateMode) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                safeBrowsingEnabled = false
                offscreenPreRaster = false
            }
            userAgentString = if (config.desktopMode) {
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
            } else {
                null
            }
        }
        runCatching {
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, !config.blockThirdPartyCookies)
        }
    }

    fun updateSettings(newConfig: BrowserSettingsConfig, reloadIfNeeded: Boolean = false) {
        settingsState = newConfig
        storageManager.saveSettings(newConfig)
        webViewRef?.let { wv ->
            applyWebViewSettings(wv, newConfig)
            if (reloadIfNeeded && !showStartPage) {
                wv.reload()
            }
        }
    }

    fun adjustZoom(delta: Int) {
        val next = (settingsState.textZoomPercent + delta).coerceIn(50, 200)
        updateSettings(settingsState.copy(textZoomPercent = next))
        Toast.makeText(context, "${strings.toastZoomLevel} $next%", Toast.LENGTH_SHORT).show()
    }

    fun executeLoadUrl(rawInput: String) {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return

        val targetUrl = when {
            trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> settingsState.defaultEngine.searchUrl + Uri.encode(trimmed)
        }

        val wasOnStartPage = showStartPage
        pendingLocalHtml = null
        inputUrlText = targetUrl
        currentUrl = targetUrl
        showStartPage = false
        if (!wasOnStartPage) {
            webViewRef?.loadUrl(targetUrl)
        }
        keyboardController?.hide()
    }

    fun openSavedLocalHtml(filePath: String, title: String) {
        try {
            val html = File(filePath).readText(Charsets.UTF_8)
            val wasOnStartPage = showStartPage
            pendingLocalHtml = html to title
            pageTitle = title
            currentUrl = "local://$title"
            inputUrlText = title
            showStartPage = false
            if (!wasOnStartPage) {
                webViewRef?.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        } catch (_: Exception) {
            Toast.makeText(context, strings.toastOpenSavedFailed, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun extractVisiblePageText(): String = suspendCancellableCoroutine { cont ->
        val wv = webViewRef
        if (wv == null || showStartPage) {
            cont.resume("")
            return@suspendCancellableCoroutine
        }
        try {
            wv.evaluateJavascript("(function(){ return document.body ? document.body.innerText : ''; })();") { raw ->
                val decoded = try {
                    if (raw == null || raw == "null") "" else JSONTokener(raw).nextValue().toString()
                } catch (_: Exception) {
                    raw?.trim('"') ?: ""
                }
                if (cont.isActive) cont.resume(decoded)
            }
        } catch (_: Exception) {
            if (cont.isActive) cont.resume("")
        }
    }

    fun downloadCurrentPageHtml() {
        val wv = webViewRef
        if (wv == null || showStartPage) {
            Toast.makeText(context, strings.toastOpenWebpageFirstToDownload, Toast.LENGTH_SHORT).show()
            return
        }
        wv.evaluateJavascript("(function(){ return document.documentElement ? document.documentElement.outerHTML : ''; })();") { raw ->
            val html = try {
                if (raw == null || raw == "null") "" else JSONTokener(raw).nextValue().toString()
            } catch (_: Exception) {
                ""
            }
            if (html.isNotBlank()) {
                storageManager.saveHtmlPageLocally(pageTitle, currentUrl, html, settingsState.language)
                    .onSuccess { updated ->
                        downloads = updated
                        Toast.makeText(context, strings.toastPageDownloaded, Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(context, strings.toastDownloadFailed, Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, strings.toastExtractFailed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun copyCurrentUrl() {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("URL", currentUrl))
        Toast.makeText(context, strings.toastCopied, Toast.LENGTH_SHORT).show()
    }

    fun shareCurrentUrl() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, currentUrl)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, strings.shareTitle))
    }

    fun openInExternalBrowser() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, strings.toastExternalFailed, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        when {
            showFindInPageBar -> {
                showFindInPageBar = false
                findQuery = ""
                webViewRef?.clearMatches()
            }
            !showStartPage && webViewRef?.canGoBack() == true -> {
                webViewRef?.goBack()
            }
            !showStartPage -> {
                showStartPage = true
                inputUrlText = ""
            }
            else -> {
                viewModel.navigateBack()
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides browserLayoutDirection) {
    Scaffold(
        containerColor = Color(0xFF0A0D14),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121622))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!showStartPage) {
                                showStartPage = true
                                inputUrlText = ""
                            } else {
                                viewModel.navigateBack()
                            }
                        },
                        modifier = Modifier.testTag("browser_back_home_button")
                    ) {
                        Icon(
                            imageVector = if (showStartPage) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Home,
                            contentDescription = if (showStartPage) strings.backToApp else strings.browserHome,
                            tint = Color.White
                        )
                    }

                    // Unified Smart Omnibox
                    OutlinedTextField(
                        value = inputUrlText,
                        onValueChange = { inputUrlText = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("browser_omnibox_input"),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = if (settingsState.privateMode) strings.omniboxPrivatePlaceholder else strings.omniboxPlaceholder,
                                fontSize = 11.sp,
                                color = TextSecondary.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when {
                                    settingsState.privateMode -> Icons.Default.VisibilityOff
                                    inputUrlText.startsWith("https://") -> Icons.Default.Lock
                                    else -> Icons.Default.Search
                                },
                                contentDescription = null,
                                tint = when {
                                    settingsState.privateMode -> AccentPurple
                                    inputUrlText.startsWith("https://") -> AccentGreen
                                    else -> PrimaryCyan
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inputUrlText.isNotBlank()) {
                                    IconButton(
                                        onClick = { inputUrlText = "" },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = strings.clear, tint = TextSecondary, modifier = Modifier.size(15.dp))
                                    }
                                }
                                if (!showStartPage) {
                                    IconButton(
                                        onClick = {
                                            if (isLoading) webViewRef?.stopLoading() else webViewRef?.reload()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                                            contentDescription = if (isLoading) strings.stop else strings.reload,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { executeLoadUrl(inputUrlText) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0A0D16),
                            unfocusedContainerColor = Color(0xFF0A0D16),
                            focusedBorderColor = if (settingsState.privateMode) AccentPurple else PrimaryCyan,
                            unfocusedBorderColor = Color(0xFF262D42),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    // Bookmark toggle button when browsing
                    if (!showStartPage) {
                        IconButton(
                            onClick = {
                                val (updated, added) = storageManager.toggleBookmark(pageTitle, currentUrl)
                                bookmarks = updated
                                Toast.makeText(
                                    context,
                                    if (added) strings.toastSavedAdded else strings.toastSavedRemoved,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("browser_top_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isCurrentBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = strings.bookmarkPage,
                                tint = if (isCurrentBookmarked) AccentAmber else Color.White
                            )
                        }
                    }

                    // ARNA AI Assistant button
                    IconButton(
                        onClick = { activeSheet = BrowserActiveSheet.AI_ASSISTANT },
                        modifier = Modifier.testTag("browser_top_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = strings.arnaAiContentDesc,
                            tint = AccentPurple
                        )
                    }

                    // Full Tools Grid Menu button
                    IconButton(
                        onClick = { activeSheet = BrowserActiveSheet.TOOLS },
                        modifier = Modifier.testTag("browser_top_tools_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = strings.browserToolsDesc,
                            tint = Color.White
                        )
                    }
                }

                // Quick Find In Page Bar
                AnimatedVisibility(visible = showFindInPageBar && !showStartPage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A2033))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = findQuery,
                            onValueChange = { q ->
                                findQuery = q
                                if (q.isNotBlank()) webViewRef?.findAllAsync(q) else webViewRef?.clearMatches()
                            },
                            placeholder = { Text(strings.findInPageHint, fontSize = 11.sp, color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0E121D),
                                unfocusedContainerColor = Color(0xFF0E121D),
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = Color(0xFF2E3650),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        IconButton(onClick = { webViewRef?.findNext(false) }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.previousMatch, tint = Color.White)
                        }
                        IconButton(onClick = { webViewRef?.findNext(true) }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.nextMatch, tint = Color.White)
                        }
                        IconButton(
                            onClick = {
                                showFindInPageBar = false
                                findQuery = ""
                                webViewRef?.clearMatches()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = strings.closeSearch, tint = TextSecondary)
                        }
                    }
                }

                // Loading Progress Bar
                if (!showStartPage && isLoading && loadingProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = { loadingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = if (settingsState.privateMode) AccentPurple else PrimaryCyan,
                        trackColor = Color.Transparent
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF121622),
                border = BorderStroke(1.dp, Color(0xFF23293E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = !showStartPage && canGoBack,
                        modifier = Modifier.testTag("browser_nav_back")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back,
                            tint = if (!showStartPage && canGoBack) Color.White else Color.Gray.copy(alpha = 0.35f)
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = !showStartPage && canGoForward,
                        modifier = Modifier.testTag("browser_nav_forward")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = strings.forward,
                            tint = if (!showStartPage && canGoForward) Color.White else Color.Gray.copy(alpha = 0.35f)
                        )
                    }

                    // Quick Translate button in bottom dock
                    IconButton(
                        onClick = { activeSheet = BrowserActiveSheet.TRANSLATE },
                        modifier = Modifier.testTag("browser_nav_translate")
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = strings.translatePage, tint = PrimaryCyan)
                    }

                    // Quick Zoom pill
                    Surface(
                        onClick = { activeSheet = BrowserActiveSheet.TOOLS },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1D2336),
                        border = BorderStroke(1.dp, Color(0xFF2F3752)),
                        modifier = Modifier.testTag("browser_nav_zoom_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${settingsState.textZoomPercent}%", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Downloads button with badge
                    IconButton(
                        onClick = { activeSheet = BrowserActiveSheet.DOWNLOADS },
                        modifier = Modifier.testTag("browser_nav_downloads")
                    ) {
                        BadgedBox(
                            badge = {
                                if (downloads.isNotEmpty()) {
                                    Badge(containerColor = AccentGreen) {
                                        Text(downloads.size.toString(), fontSize = 9.sp, color = DarkBackground)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = strings.downloads, tint = AccentGreen)
                        }
                    }

                    IconButton(
                        onClick = { activeSheet = BrowserActiveSheet.SETTINGS },
                        modifier = Modifier.testTag("browser_nav_settings")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = strings.settings, tint = TextSecondary)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showStartPage) {
                // ================= MINIMAL ARNA BROWSER CANVAS =================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0A0E1A), Color(0xFF111728), Color(0xFF0A0D14))
                            )
                        )
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Compact ARNA Brand + Quick Language Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryCyan.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = strings.browserName,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = strings.browserName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = strings.aiName,
                                    color = AccentPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // 1-Tap In-Browser Arabic / English Switcher Pill
                        Surface(
                            color = Color(0xFF151B2E),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF28324F)),
                            modifier = Modifier.testTag("browser_start_lang_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrowserLanguage.entries.forEach { lang ->
                                    val selected = settingsState.language == lang
                                    Surface(
                                        onClick = { updateSettings(settingsState.copy(language = lang)) },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (selected) PrimaryCyan.copy(alpha = 0.22f) else Color.Transparent,
                                        modifier = Modifier.testTag(
                                            if (lang == BrowserLanguage.ARABIC) "start_lang_ar" else "start_lang_en"
                                        )
                                    ) {
                                        Text(
                                            text = lang.nativeLabel,
                                            color = if (selected) PrimaryCyan else TextSecondary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (bookmarks.isEmpty() && historyItems.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = PrimaryCyan.copy(alpha = 0.65f),
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = strings.startCanvasHint,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // User's Own Saved Pages (Bookmarks) if any
                    if (bookmarks.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.savedPagesTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = strings.viewAll,
                                color = PrimaryCyan,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { activeSheet = BrowserActiveSheet.BOOKMARKS }
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            bookmarks.take(4).forEach { bm ->
                                Surface(
                                    onClick = { executeLoadUrl(bm.url) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF141A2C),
                                    border = BorderStroke(1.dp, Color(0xFF252D46)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(bm.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(bm.url, color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Recent History Preview if any
                    if (historyItems.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.recentHistoryTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = strings.manageHistory,
                                color = PrimaryCyan,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { activeSheet = BrowserActiveSheet.HISTORY }
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            historyItems.take(3).forEach { entry ->
                                Surface(
                                    onClick = { executeLoadUrl(entry.url) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF141A2C),
                                    border = BorderStroke(1.dp, Color(0xFF252D46)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(entry.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(entry.url, color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ================= HARDENED WEBVIEW RENDERER =================
                key(webViewGeneration) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                if (isEmulatorEnvironment()) {
                                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                }
                                applyWebViewSettings(this, settingsState)

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, true)
                                }

                                setDownloadListener { dlUrl, _, contentDisposition, mimeType, contentLength ->
                                    storageManager.enqueueSystemDownload(
                                        dlUrl,
                                        contentDisposition,
                                        mimeType,
                                        contentLength,
                                        settingsState.language
                                    )
                                        .onSuccess { updated ->
                                            downloads = updated
                                            Toast.makeText(ctx, strings.toastDownloadStarted, Toast.LENGTH_SHORT).show()
                                        }
                                        .onFailure {
                                            Toast.makeText(ctx, strings.toastDownloadStartFailed, Toast.LENGTH_SHORT).show()
                                        }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        isLoading = true
                                        url?.let {
                                            if (!it.startsWith("data:")) {
                                                currentUrl = it
                                                inputUrlText = it
                                            }
                                        }
                                        canGoBack = view?.canGoBack() ?: false
                                        canGoForward = view?.canGoForward() ?: false
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isLoading = false
                                        val finalTitle = view?.title?.takeIf { it.isNotBlank() } ?: pageTitle
                                        pageTitle = finalTitle
                                        canGoBack = view?.canGoBack() ?: false
                                        canGoForward = view?.canGoForward() ?: false

                                        if (!url.isNullOrBlank() && url != "about:blank" && !url.startsWith("data:")) {
                                            currentUrl = url
                                            inputUrlText = url
                                            if (!settingsState.privateMode) {
                                                historyItems = storageManager.addHistoryEntry(finalTitle, url)
                                            }
                                        }
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val urlStr = request?.url?.toString() ?: return false
                                        return if (urlStr.startsWith("http://", ignoreCase = true) ||
                                            urlStr.startsWith("https://", ignoreCase = true)
                                        ) {
                                            false
                                        } else {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
                                                ctx.startActivity(intent)
                                                true
                                            } catch (_: Exception) {
                                                true
                                            }
                                        }
                                    }

                                    override fun onRenderProcessGone(
                                        view: WebView?,
                                        detail: RenderProcessGoneDetail?
                                    ): Boolean {
                                        if (webViewRef === view) {
                                            webViewRef = null
                                        }
                                        safelyDestroyWebView(view)
                                        isLoading = false
                                        val now = System.currentTimeMillis()
                                        val rapidCrash = (now - lastCrashTimestamp) < 5_000L
                                        lastCrashTimestamp = now
                                        if (rapidCrash) {
                                            showStartPage = true
                                            inputUrlText = ""
                                        } else {
                                            webViewGeneration++
                                        }
                                        Toast.makeText(
                                            ctx,
                                            strings.toastRendererRecovered,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return true
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        if (request?.isForMainFrame == true) {
                                            isLoading = false
                                        }
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        super.onProgressChanged(view, newProgress)
                                        loadingProgress = newProgress
                                        isLoading = newProgress < 100
                                        canGoBack = view?.canGoBack() ?: false
                                        canGoForward = view?.canGoForward() ?: false
                                    }

                                    override fun onReceivedTitle(view: WebView?, title: String?) {
                                        super.onReceivedTitle(view, title)
                                        if (!title.isNullOrBlank()) {
                                            pageTitle = title
                                        }
                                    }
                                }

                                val localSnap = pendingLocalHtml
                                if (localSnap != null) {
                                    loadDataWithBaseURL(null, localSnap.first, "text/html", "UTF-8", null)
                                } else if (currentUrl.isNotBlank() && currentUrl != "about:blank") {
                                    loadUrl(currentUrl)
                                }
                                webViewRef = this
                            }
                        },
                        update = { webView ->
                            webViewRef = webView
                            if (webView.settings.textZoom != settingsState.textZoomPercent) {
                                webView.settings.textZoom = settingsState.textZoomPercent
                            }
                        },
                        onRelease = { webView ->
                            if (webViewRef === webView) {
                                webViewRef = null
                            }
                            safelyDestroyWebView(webView)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // ================= MODAL SHEETS =================
    when (activeSheet) {
        BrowserActiveSheet.TOOLS -> {
            BrowserToolsMenuSheet(
                strings = strings,
                language = settingsState.language,
                onChangeLanguage = { lang ->
                    updateSettings(settingsState.copy(language = lang))
                },
                isOnStartPage = showStartPage,
                isBookmarked = isCurrentBookmarked,
                isDesktopMode = settingsState.desktopMode,
                zoomPercent = settingsState.textZoomPercent,
                historyCount = historyItems.size,
                bookmarksCount = bookmarks.size,
                downloadsCount = downloads.size,
                onDismiss = { activeSheet = BrowserActiveSheet.NONE },
                onOpenAi = { activeSheet = BrowserActiveSheet.AI_ASSISTANT },
                onOpenTranslate = { activeSheet = BrowserActiveSheet.TRANSLATE },
                onToggleBookmark = {
                    if (showStartPage) {
                        Toast.makeText(context, strings.toastOpenWebpageFirstToSave, Toast.LENGTH_SHORT).show()
                    } else {
                        val (updated, added) = storageManager.toggleBookmark(pageTitle, currentUrl)
                        bookmarks = updated
                        Toast.makeText(context, if (added) strings.toastSavedAdded else strings.toastSavedRemoved, Toast.LENGTH_SHORT).show()
                    }
                },
                onDownloadCurrentPage = { downloadCurrentPageHtml() },
                onOpenDownloads = { activeSheet = BrowserActiveSheet.DOWNLOADS },
                onOpenHistory = { activeSheet = BrowserActiveSheet.HISTORY },
                onOpenBookmarks = { activeSheet = BrowserActiveSheet.BOOKMARKS },
                onOpenFindInPage = { showFindInPageBar = true },
                onToggleDesktopMode = {
                    updateSettings(settingsState.copy(desktopMode = !settingsState.desktopMode), reloadIfNeeded = true)
                },
                onZoomOut = { adjustZoom(-10) },
                onZoomIn = { adjustZoom(10) },
                onZoomReset = {
                    updateSettings(settingsState.copy(textZoomPercent = 100))
                    Toast.makeText(context, strings.toastZoomReset, Toast.LENGTH_SHORT).show()
                },
                onCopyUrl = { copyCurrentUrl() },
                onShareUrl = { shareCurrentUrl() },
                onOpenExternal = { openInExternalBrowser() },
                onOpenSettings = { activeSheet = BrowserActiveSheet.SETTINGS }
            )
        }

        BrowserActiveSheet.AI_ASSISTANT -> {
            BrowserAiAssistantSheet(
                strings = strings,
                language = settingsState.language,
                pageTitle = pageTitle,
                pageUrl = currentUrl,
                isOnStartPage = showStartPage,
                isLoading = aiBusy,
                aiResponse = aiResponse,
                aiNoticeText = aiRepository.statusNotice(settingsState.language),
                onDismiss = { activeSheet = BrowserActiveSheet.NONE },
                onAskPrompt = { prompt ->
                    coroutineScope.launch {
                        aiBusy = true
                        val excerpt = extractVisiblePageText()
                        aiResponse = aiRepository.askAssistant(
                            prompt = prompt,
                            pageTitle = pageTitle,
                            pageUrl = currentUrl,
                            pageExcerpt = excerpt,
                            language = settingsState.language
                        )
                        aiBusy = false
                    }
                },
                onSummarizePage = {
                    coroutineScope.launch {
                        aiBusy = true
                        val text = extractVisiblePageText()
                        aiResponse = aiRepository.summarizePage(
                            pageTitle = pageTitle,
                            pageUrl = currentUrl,
                            pageText = text,
                            language = settingsState.language
                        )
                        aiBusy = false
                    }
                },
                onOpenTranslateSheet = {
                    activeSheet = BrowserActiveSheet.TRANSLATE
                }
            )
        }

        BrowserActiveSheet.TRANSLATE -> {
            BrowserTranslateSheet(
                strings = strings,
                language = settingsState.language,
                pageTitle = if (showStartPage) strings.defaultWebTitle else pageTitle,
                selectedLanguage = translateLanguage,
                onLanguageSelected = { translateLanguage = it },
                isLoading = aiBusy,
                translationResult = translateResult,
                aiNoticeText = aiRepository.statusNotice(settingsState.language),
                onTranslateNow = {
                    coroutineScope.launch {
                        aiBusy = true
                        val text = extractVisiblePageText()
                        translateResult = aiRepository.translatePage(
                            pageTitle = if (showStartPage) strings.defaultWebTitle else pageTitle,
                            pageText = text,
                            targetLanguage = translateLanguage,
                            language = settingsState.language
                        )
                        aiBusy = false
                    }
                },
                onDismiss = { activeSheet = BrowserActiveSheet.NONE }
            )
        }

        BrowserActiveSheet.BOOKMARKS -> {
            BrowserBookmarksSheet(
                strings = strings,
                language = settingsState.language,
                bookmarks = bookmarks,
                onOpenUrl = { url -> executeLoadUrl(url) },
                onDeleteBookmark = { id -> bookmarks = storageManager.removeBookmark(id) },
                onDismiss = { activeSheet = BrowserActiveSheet.NONE }
            )
        }

        BrowserActiveSheet.HISTORY -> {
            BrowserHistorySheet(
                strings = strings,
                language = settingsState.language,
                historyItems = historyItems,
                onOpenUrl = { url -> executeLoadUrl(url) },
                onDeleteItem = { id -> historyItems = storageManager.removeHistoryEntry(id) },
                onClearAll = {
                    historyItems = storageManager.clearHistory()
                    Toast.makeText(context, strings.toastHistoryCleared, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { activeSheet = BrowserActiveSheet.NONE }
            )
        }

        BrowserActiveSheet.DOWNLOADS -> {
            BrowserDownloadsSheet(
                strings = strings,
                language = settingsState.language,
                downloads = downloads,
                onOpenUrl = { url -> executeLoadUrl(url) },
                onOpenLocalHtml = { path, title -> openSavedLocalHtml(path, title) },
                onDeleteItem = { id -> downloads = storageManager.removeDownloadOperation(id) },
                onClearAll = { downloads = storageManager.clearDownloads() },
                onOpenMediaDownloader = { viewModel.navigateTo(AppScreen.MediaDownloader) },
                onDismiss = { activeSheet = BrowserActiveSheet.NONE }
            )
        }

        BrowserActiveSheet.SETTINGS -> {
            BrowserSettingsSheet(
                strings = strings,
                settings = settingsState,
                onUpdateSettings = { updated ->
                    val shouldReload = updated.desktopMode != settingsState.desktopMode ||
                        updated.javaScriptEnabled != settingsState.javaScriptEnabled
                    updateSettings(updated, reloadIfNeeded = shouldReload)
                },
                onClearCacheAndCookies = {
                    webViewRef?.clearCache(true)
                    CookieManager.getInstance().removeAllCookies(null)
                    WebStorage.getInstance().deleteAllData()
                    Toast.makeText(context, strings.toastCacheCleared, Toast.LENGTH_SHORT).show()
                },
                onClearHistory = {
                    historyItems = storageManager.clearHistory()
                    Toast.makeText(context, strings.toastHistoryCleared, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { activeSheet = BrowserActiveSheet.NONE }
            )
        }

        BrowserActiveSheet.NONE -> Unit
    }
    }
}
