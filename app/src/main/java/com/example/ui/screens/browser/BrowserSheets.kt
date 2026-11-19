package com.example.ui.screens.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.BrowserAiResponse
import com.example.data.browser.BrowserDownloadOperation
import com.example.data.browser.BrowserHistoryItem
import com.example.data.browser.BrowserLanguage
import com.example.data.browser.BrowserSearchEngine
import com.example.data.browser.BrowserSettingsConfig
import com.example.data.browser.SavedBrowserPage
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SheetSurfaceColor = Color(0xFF151926)
private val CardBgColor = Color(0xFF1D2233)
private val CardBorderColor = Color(0xFF2D354D)

private fun layoutDirectionFor(language: BrowserLanguage): LayoutDirection =
    if (language == BrowserLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

@Composable
fun BrowserLanguageSelectorPill(
    selectedLanguage: BrowserLanguage,
    label: String,
    onSelectLanguage: (BrowserLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardBgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BrowserLanguage.entries.forEach { lang ->
                    val selected = lang == selectedLanguage
                    Surface(
                        onClick = { onSelectLanguage(lang) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) PrimaryCyan.copy(alpha = 0.2f) else Color(0xFF283047),
                        border = BorderStroke(
                            1.dp,
                            if (selected) PrimaryCyan else Color.Transparent
                        ),
                        modifier = Modifier.testTag(
                            if (lang == BrowserLanguage.ARABIC) "browser_lang_ar_button" else "browser_lang_en_button"
                        )
                    ) {
                        Text(
                            text = lang.nativeLabel,
                            color = if (selected) PrimaryCyan else TextSecondary,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserToolsMenuSheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    onChangeLanguage: (BrowserLanguage) -> Unit,
    isOnStartPage: Boolean,
    isBookmarked: Boolean,
    isDesktopMode: Boolean,
    zoomPercent: Int,
    historyCount: Int,
    bookmarksCount: Int,
    downloadsCount: Int,
    onDismiss: () -> Unit,
    onOpenAi: () -> Unit,
    onOpenTranslate: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDownloadCurrentPage: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenFindInPage: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomReset: () -> Unit,
    onCopyUrl: () -> Unit,
    onShareUrl: () -> Unit,
    onOpenExternal: () -> Unit,
    onOpenSettings: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.toolsCenterTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("browser_tools_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                    }
                }

                // In-browser Arabic / English Language Switcher
                BrowserLanguageSelectorPill(
                    selectedLanguage = language,
                    label = strings.languageLabel,
                    onSelectLanguage = onChangeLanguage
                )

                // Zoom Bar inside Tools Menu
                Surface(
                    color = CardBgColor,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ZoomIn, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.zoomLabel, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = onZoomOut,
                                shape = CircleShape,
                                color = Color(0xFF283047),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("browser_zoom_out_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = strings.zoomOut, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = onZoomReset,
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryCyan.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.45f)),
                                modifier = Modifier.testTag("browser_zoom_reset_button")
                            ) {
                                Text(
                                    text = "$zoomPercent%",
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                onClick = onZoomIn,
                                shape = CircleShape,
                                color = Color(0xFF283047),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("browser_zoom_in_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = strings.zoomIn, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Primary Tools Grid (3 columns)
                val toolActions = listOf(
                    BrowserToolTileData(strings.aiName, Icons.Default.AutoAwesome, AccentPurple, "tool_ai", onOpenAi),
                    BrowserToolTileData(strings.translatePage, Icons.Default.Translate, PrimaryCyan, "tool_translate", onOpenTranslate),
                    BrowserToolTileData(
                        if (isBookmarked) strings.savedPageBadge else strings.savePage,
                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        AccentAmber,
                        "tool_save_page",
                        onToggleBookmark
                    ),
                    BrowserToolTileData(strings.downloadPage, Icons.Default.FileDownload, AccentGreen, "tool_download_page", onDownloadCurrentPage),
                    BrowserToolTileData("${strings.downloadOps} ($downloadsCount)", Icons.Default.Download, Color(0xFF3B82F6), "tool_downloads", onOpenDownloads),
                    BrowserToolTileData("${strings.history} ($historyCount)", Icons.Default.History, Color(0xFFF97316), "tool_history", onOpenHistory),
                    BrowserToolTileData("${strings.bookmarks} ($bookmarksCount)", Icons.Default.Bookmark, Color(0xFFEAB308), "tool_bookmarks", onOpenBookmarks),
                    BrowserToolTileData(
                        if (isDesktopMode) strings.mobileMode else strings.desktopMode,
                        if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                        Color(0xFF06B6D4),
                        "tool_desktop",
                        onToggleDesktopMode
                    ),
                    BrowserToolTileData(strings.settings, Icons.Default.Settings, Color(0xFF94A3B8), "tool_settings", onOpenSettings)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    toolActions.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { item ->
                                Surface(
                                    onClick = {
                                        onDismiss()
                                        item.onClick()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardBgColor,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(item.tag)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = item.tint.copy(alpha = 0.16f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.label,
                                                    tint = item.tint,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (!isOnStartPage) {
                    HorizontalDivider(color = CardBorderColor)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactSecondaryAction(
                            label = strings.findInPage,
                            icon = Icons.Default.FindInPage,
                            modifier = Modifier.weight(1f).testTag("tool_find_in_page"),
                            onClick = {
                                onDismiss()
                                onOpenFindInPage()
                            }
                        )
                        CompactSecondaryAction(
                            label = strings.copyUrl,
                            icon = Icons.Default.ContentCopy,
                            modifier = Modifier.weight(1f).testTag("tool_copy_url"),
                            onClick = {
                                onDismiss()
                                onCopyUrl()
                            }
                        )
                        CompactSecondaryAction(
                            label = strings.shareUrl,
                            icon = Icons.Default.Share,
                            modifier = Modifier.weight(1f).testTag("tool_share_url"),
                            onClick = {
                                onDismiss()
                                onShareUrl()
                            }
                        )
                        CompactSecondaryAction(
                            label = strings.externalBrowser,
                            icon = Icons.Default.Launch,
                            modifier = Modifier.weight(1f).testTag("tool_external_browser"),
                            onClick = {
                                onDismiss()
                                onOpenExternal()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

private data class BrowserToolTileData(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
    val tag: String,
    val onClick: () -> Unit
)

@Composable
private fun CompactSecondaryAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CardBgColor,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
            Text(label, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserAiAssistantSheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    pageTitle: String,
    pageUrl: String,
    isOnStartPage: Boolean,
    isLoading: Boolean,
    aiResponse: BrowserAiResponse?,
    aiNoticeText: String,
    onDismiss: () -> Unit,
    onAskPrompt: (String) -> Unit,
    onSummarizePage: () -> Unit,
    onOpenTranslateSheet: () -> Unit
) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AccentPurple.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(strings.aiTitle, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(
                                text = if (isOnStartPage) strings.aiSubtitleStart else pageTitle,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                    }
                }

                // Firebase Notice Banner
                Surface(
                    color = AccentAmber.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = aiNoticeText,
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Quick Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = onSummarizePage,
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryCyan.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.testTag("ai_summarize_page_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Summarize, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.summarizePage, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        onClick = {
                            onDismiss()
                            onOpenTranslateSheet()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = AccentGreen.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.45f)),
                        modifier = Modifier.testTag("ai_open_translate_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.translatePage, color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        onClick = {
                            val q = strings.extractIdeasPrompt
                            promptText = q
                            onAskPrompt(q)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = AccentPurple.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = strings.extractIdeas,
                            color = Color(0xFFD8B4FE),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_prompt_input"),
                    placeholder = { Text(strings.aiPromptHint, color = TextSecondary, fontSize = 12.sp) },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0E111B),
                        unfocusedContainerColor = Color(0xFF0E111B),
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        if (promptText.isNotBlank()) onAskPrompt(promptText.trim())
                    },
                    enabled = !isLoading && promptText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("ai_send_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.aiProcessing, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.aiSend, fontWeight = FontWeight.Bold)
                    }
                }

                if (aiResponse != null) {
                    Surface(
                        color = CardBgColor,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (aiResponse.isSampleMode) strings.aiSampleResultLabel else strings.aiLiveResultLabel,
                                    color = AccentPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("ARNA AI Result", aiResponse.content))
                                        Toast.makeText(context, strings.toastCopied, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = strings.copy, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = aiResponse.content,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserTranslateSheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    pageTitle: String,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    isLoading: Boolean,
    translationResult: BrowserAiResponse?,
    aiNoticeText: String,
    onTranslateNow: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val languages = listOf("العربية", "English", "Français", "Türkçe", "Español", "Deutsch")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(strings.translateTitle, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(pageTitle, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                    }
                }

                Surface(
                    color = AccentAmber.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(aiNoticeText, color = Color(0xFFFDE68A), fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }

                Text(strings.chooseTargetLanguage, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { lang ->
                        val selected = lang == selectedLanguage
                        Surface(
                            onClick = { onLanguageSelected(lang) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) PrimaryCyan.copy(alpha = 0.2f) else CardBgColor,
                            border = BorderStroke(1.dp, if (selected) PrimaryCyan else CardBorderColor)
                        ) {
                            Text(
                                text = lang,
                                color = if (selected) PrimaryCyan else TextPrimary,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onTranslateNow,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("translate_execute_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DarkBackground, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.translatingProgress, color = DarkBackground, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${strings.translateNowPrefix} $selectedLanguage", color = DarkBackground, fontWeight = FontWeight.Black)
                    }
                }

                if (translationResult != null) {
                    Surface(
                        color = CardBgColor,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(strings.translatedResultTitle, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("Translated Text", translationResult.content))
                                        Toast.makeText(context, strings.toastCopied, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = strings.copy, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(translationResult.content, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserBookmarksSheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    bookmarks: List<SavedBrowserPage>,
    onOpenUrl: (String) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${strings.savedPagesHeader} (${bookmarks.size})", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                    }
                }

                if (bookmarks.isEmpty()) {
                    EmptyBrowserSheetState(
                        icon = Icons.Default.BookmarkBorder,
                        title = strings.noSavedPagesTitle,
                        subtitle = strings.noSavedPagesSubtitle
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks, key = { it.id }) { item ->
                            Surface(
                                onClick = {
                                    onDismiss()
                                    onOpenUrl(item.url)
                                },
                                color = CardBgColor,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AccentAmber.copy(alpha = 0.16f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(item.url, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    IconButton(onClick = { onDeleteBookmark(item.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserHistorySheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    historyItems: List<BrowserHistoryItem>,
    onOpenUrl: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    var filterText by remember { mutableStateOf("") }
    val filtered = remember(historyItems, filterText) {
        if (filterText.isBlank()) historyItems
        else historyItems.filter {
            it.title.contains(filterText, ignoreCase = true) ||
                it.url.contains(filterText, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${strings.historyHeader} (${historyItems.size})", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (historyItems.isNotEmpty()) {
                            TextButton(onClick = onClearAll, modifier = Modifier.testTag("history_clear_all_button")) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.clearAll, color = Color(0xFFF87171), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                        }
                    }
                }

                if (historyItems.isNotEmpty()) {
                    OutlinedTextField(
                        value = filterText,
                        onValueChange = { filterText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(strings.searchHistoryHint, color = TextSecondary, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                        trailingIcon = {
                            if (filterText.isNotEmpty()) {
                                IconButton(onClick = { filterText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = strings.clear, tint = TextSecondary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0E111B),
                            unfocusedContainerColor = Color(0xFF0E111B),
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                if (filtered.isEmpty()) {
                    EmptyBrowserSheetState(
                        icon = Icons.Default.History,
                        title = if (historyItems.isEmpty()) strings.emptyHistoryTitle else strings.noMatchingHistoryTitle,
                        subtitle = strings.emptyHistorySubtitle
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { entry ->
                            Surface(
                                onClick = {
                                    onDismiss()
                                    onOpenUrl(entry.url)
                                },
                                color = CardBgColor,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Public, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(entry.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(entry.url, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(formatTimestamp(entry.visitedAt), color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp)
                                    }
                                    IconButton(onClick = { onDeleteItem(entry.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserDownloadsSheet(
    strings: BrowserStrings,
    language: BrowserLanguage,
    downloads: List<BrowserDownloadOperation>,
    onOpenUrl: (String) -> Unit,
    onOpenLocalHtml: (String, String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onOpenMediaDownloader: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${strings.downloadsHeader} (${downloads.size})", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (downloads.isNotEmpty()) {
                            TextButton(onClick = onClearAll) {
                                Text(strings.clearShort, color = Color(0xFFF87171), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                        }
                    }
                }

                Surface(
                    onClick = {
                        onDismiss()
                        onOpenMediaDownloader()
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1D4ED8).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(strings.mediaDownloaderTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(strings.mediaDownloaderSubtitle, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                    }
                }

                if (downloads.isEmpty()) {
                    EmptyBrowserSheetState(
                        icon = Icons.Default.Download,
                        title = strings.emptyDownloadsTitle,
                        subtitle = strings.emptyDownloadsSubtitle
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(downloads, key = { it.id }) { op ->
                            Surface(
                                onClick = {
                                    onDismiss()
                                    if (op.localFilePath != null) {
                                        onOpenLocalHtml(op.localFilePath, op.fileName)
                                    } else if (op.url.isNotBlank()) {
                                        onOpenUrl(op.url)
                                    }
                                },
                                color = CardBgColor,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AccentGreen.copy(alpha = 0.16f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(op.fileName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        val sizeLabel = if (op.sizeBytes > 0) Formatter.formatShortFileSize(context, op.sizeBytes) else op.mimeType
                                        Text("${op.status} • $sizeLabel", color = AccentGreen, fontSize = 11.sp)
                                        Text(formatTimestamp(op.createdAt), color = TextSecondary, fontSize = 10.sp)
                                    }
                                    IconButton(onClick = { onDeleteItem(op.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserSettingsSheet(
    strings: BrowserStrings,
    settings: BrowserSettingsConfig,
    onUpdateSettings: (BrowserSettingsConfig) -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SheetSurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirectionFor(settings.language)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.settingsHeader, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = strings.close, tint = TextSecondary)
                    }
                }

                // In-browser Arabic / English Language Setting
                BrowserLanguageSelectorPill(
                    selectedLanguage = settings.language,
                    label = strings.browserLanguageSettingTitle,
                    onSelectLanguage = { lang -> onUpdateSettings(settings.copy(language = lang)) }
                )

                // Default search engine selector
                Text(strings.defaultSearchEngine, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BrowserSearchEngine.entries.forEach { engine ->
                        val selected = settings.defaultEngine == engine
                        Surface(
                            onClick = { onUpdateSettings(settings.copy(defaultEngine = engine)) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) PrimaryCyan.copy(alpha = 0.2f) else CardBgColor,
                            border = BorderStroke(1.dp, if (selected) PrimaryCyan else CardBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(engine.badge, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = engine.displayTitle(settings.language),
                                    color = if (selected) PrimaryCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Text zoom slider
                Surface(
                    color = CardBgColor,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.defaultZoomLabel, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${settings.textZoomPercent}%", color = PrimaryCyan, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                        Slider(
                            value = settings.textZoomPercent.toFloat(),
                            onValueChange = { value ->
                                val snapped = ((value / 10f).toInt() * 10).coerceIn(50, 200)
                                onUpdateSettings(settings.copy(textZoomPercent = snapped))
                            },
                            valueRange = 50f..200f,
                            steps = 14,
                            colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan)
                        )
                    }
                }

                BrowserSettingSwitchRow(
                    title = strings.jsTitle,
                    subtitle = strings.jsSubtitle,
                    icon = Icons.Default.Public,
                    checked = settings.javaScriptEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(javaScriptEnabled = it)) }
                )

                BrowserSettingSwitchRow(
                    title = strings.desktopSettingTitle,
                    subtitle = strings.desktopSettingSubtitle,
                    icon = Icons.Default.Computer,
                    checked = settings.desktopMode,
                    onCheckedChange = { onUpdateSettings(settings.copy(desktopMode = it)) }
                )

                BrowserSettingSwitchRow(
                    title = strings.privateModeTitle,
                    subtitle = strings.privateModeSubtitle,
                    icon = Icons.Default.VisibilityOff,
                    checked = settings.privateMode,
                    onCheckedChange = { onUpdateSettings(settings.copy(privateMode = it)) }
                )

                BrowserSettingSwitchRow(
                    title = strings.blockPopupsTitle,
                    subtitle = strings.blockPopupsSubtitle,
                    icon = Icons.Default.Security,
                    checked = settings.blockPopups,
                    onCheckedChange = { onUpdateSettings(settings.copy(blockPopups = it)) }
                )

                BrowserSettingSwitchRow(
                    title = strings.blockCookiesTitle,
                    subtitle = strings.blockCookiesSubtitle,
                    icon = Icons.Default.Security,
                    checked = settings.blockThirdPartyCookies,
                    onCheckedChange = { onUpdateSettings(settings.copy(blockThirdPartyCookies = it)) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onClearCacheAndCookies,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF283047)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(strings.clearCacheCookies, color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.22f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(strings.clearHistoryButton, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun BrowserSettingSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = CardBgColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryCyan,
                    checkedTrackColor = PrimaryCyan.copy(alpha = 0.45f)
                )
            )
        }
    }
}

@Composable
private fun EmptyBrowserSheetState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        color = CardBgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(34.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    return try {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(millis))
    } catch (_: Exception) {
        ""
    }
}
