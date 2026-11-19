package com.example.data.ai

import com.example.data.browser.BrowserLanguage
import kotlinx.coroutines.delay

/**
 * Result returned by ARNA AI operations (Assistant, Translation, Summarization).
 */
data class BrowserAiResponse(
    val content: String,
    val isSampleMode: Boolean = true,
    val noticeText: String
)

/**
 * Abstraction for ARNA AI capabilities inside the ARNA browser.
 * Backed by PlaceholderBrowserAiRepository until Firebase AI Logic is enabled.
 */
interface BrowserAiRepository {
    val isLiveAiEnabled: Boolean
    fun statusNotice(language: BrowserLanguage): String

    suspend fun askAssistant(
        prompt: String,
        pageTitle: String,
        pageUrl: String,
        pageExcerpt: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): BrowserAiResponse

    suspend fun translatePage(
        pageTitle: String,
        pageText: String,
        targetLanguage: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): BrowserAiResponse

    suspend fun summarizePage(
        pageTitle: String,
        pageUrl: String,
        pageText: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): BrowserAiResponse
}

class PlaceholderBrowserAiRepository : BrowserAiRepository {
    override val isLiveAiEnabled: Boolean = false

    override fun statusNotice(language: BrowserLanguage): String {
        return if (language == BrowserLanguage.ENGLISH) {
            "Note: ARNA AI live Gemini features require Firebase to be enabled. Outputs shown here are labeled sample responses."
        } else {
            "ملاحظة: ميزات ARNA AI المباشرة (Gemini) تتطلب تفعيل Firebase. النتائج المعروضة حالياً هي ردود نموذجية تجريبية."
        }
    }

    override suspend fun askAssistant(
        prompt: String,
        pageTitle: String,
        pageUrl: String,
        pageExcerpt: String,
        language: BrowserLanguage
    ): BrowserAiResponse {
        delay(350)
        val notice = statusNotice(language)
        if (language == BrowserLanguage.ENGLISH) {
            val contextHint = if (pageUrl.isNotBlank() && pageUrl != "about:blank") {
                "Regarding current page: \"$pageTitle\" ($pageUrl)\n"
            } else {
                ""
            }
            val excerptSnippet = pageExcerpt.trim().take(180).let {
                if (it.isNotBlank()) "\nPage excerpt:\n\"$it...\"\n" else ""
            }
            return BrowserAiResponse(
                content = buildString {
                    appendLine("✨ [ARNA AI Sample Response — Firebase Required]")
                    appendLine()
                    append(contextHint)
                    appendLine("Your question: \"$prompt\"")
                    if (excerptSnippet.isNotBlank()) appendLine(excerptSnippet)
                    appendLine("• Initial overview: Once Firebase is enabled, ARNA AI will analyze the full page content and answer your question directly.")
                },
                isSampleMode = true,
                noticeText = notice
            )
        }

        val contextHint = if (pageUrl.isNotBlank() && pageUrl != "about:blank") {
            "بخصوص الصفحة الحالية: \"$pageTitle\" ($pageUrl)\n"
        } else {
            ""
        }
        val excerptSnippet = pageExcerpt.trim().take(180).let {
            if (it.isNotBlank()) "\nمقتطف من محتوى الصفحة:\n«$it...»\n" else ""
        }
        return BrowserAiResponse(
            content = buildString {
                appendLine("✨ [رد ARNA AI نموذجي — يتطلب تفعيل Firebase]")
                appendLine()
                append(contextHint)
                appendLine("سؤالك: «$prompt»")
                if (excerptSnippet.isNotBlank()) appendLine(excerptSnippet)
                appendLine("• التحليل المبدئي: عند تفعيل Firebase، سيقوم مساعد ARNA AI بتحليل محتوى الصفحة بدقة والإجابة على سؤالك وتنفيذ المهام البحثية مباشرة.")
            },
            isSampleMode = true,
            noticeText = notice
        )
    }

    override suspend fun translatePage(
        pageTitle: String,
        pageText: String,
        targetLanguage: String,
        language: BrowserLanguage
    ): BrowserAiResponse {
        delay(350)
        val notice = statusNotice(language)
        val cleaned = pageText.trim().replace(Regex("\\s+"), " ").take(450)
        if (language == BrowserLanguage.ENGLISH) {
            return BrowserAiResponse(
                content = buildString {
                    appendLine("🌐 [ARNA AI Sample Translation to: $targetLanguage — Firebase Required]")
                    appendLine("Title: $pageTitle")
                    appendLine()
                    if (cleaned.isNotBlank()) {
                        appendLine("Extracted page text:")
                        appendLine(cleaned)
                        appendLine()
                    }
                    appendLine("• Full live webpage translation runs via ARNA AI + Gemini once Firebase is enabled in project settings.")
                },
                isSampleMode = true,
                noticeText = notice
            )
        }
        return BrowserAiResponse(
            content = buildString {
                appendLine("🌐 [ترجمة ARNA AI نموذجية إلى: $targetLanguage — تتطلب تفعيل Firebase]")
                appendLine("العنوان: $pageTitle")
                appendLine()
                if (cleaned.isNotBlank()) {
                    appendLine("النص المستخرج من الصفحة:")
                    appendLine(cleaned)
                    appendLine()
                }
                appendLine("• الترجمة الكاملة الفورية لصفحات الويب تعمل عبر ARNA AI عند تفعيل Firebase في إعدادات المشروع.")
            },
            isSampleMode = true,
            noticeText = notice
        )
    }

    override suspend fun summarizePage(
        pageTitle: String,
        pageUrl: String,
        pageText: String,
        language: BrowserLanguage
    ): BrowserAiResponse {
        delay(350)
        val notice = statusNotice(language)
        val sentences = pageText.trim()
            .split(Regex("[.،؛\n]+"))
            .map { it.trim() }
            .filter { it.length > 18 }
            .take(4)

        if (language == BrowserLanguage.ENGLISH) {
            return BrowserAiResponse(
                content = buildString {
                    appendLine("📝 [ARNA AI Sample Summary — Firebase Required]")
                    appendLine("Title: $pageTitle")
                    appendLine()
                    if (sentences.isNotEmpty()) {
                        appendLine("Key extracted highlights:")
                        sentences.forEachIndexed { idx, line ->
                            appendLine("${idx + 1}. ${line.take(140)}")
                        }
                    } else {
                        appendLine("Open any article or text-rich webpage to summarize its key points, and enable Firebase for full ARNA AI Gemini summaries.")
                    }
                },
                isSampleMode = true,
                noticeText = notice
            )
        }

        return BrowserAiResponse(
            content = buildString {
                appendLine("📝 [ملخص ARNA AI نموذجي — يتطلب تفعيل Firebase]")
                appendLine("العنوان: $pageTitle")
                appendLine()
                if (sentences.isNotEmpty()) {
                    appendLine("أبرز النقاط المستخلصة محلياً من الصفحة:")
                    sentences.forEachIndexed { idx, line ->
                        appendLine("${idx + 1}. ${line.take(140)}")
                    }
                } else {
                    appendLine("افتح أي مقال أو صفحة ويب تحتوي على نصوص لاستخلاص ملخصها، ويمكن ربط ARNA AI الكامل عبر Firebase.")
                }
            },
            isSampleMode = true,
            noticeText = notice
        )
    }
}
