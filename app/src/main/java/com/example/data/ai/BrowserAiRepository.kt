package com.example.data.ai

import com.example.data.browser.BrowserLanguage
import kotlinx.coroutines.delay

enum class BrowserAiTaskMode(val titleAr: String, val titleEn: String) {
    CREATE("إنشاء أي شيء", "Create Anything"),
    GENERATE("توليد أي شيء", "Generate Anything"),
    EDIT("تعديل أي شيء", "Edit Anything"),
    SUMMARIZE("تلخيص الصفحة", "Summarize Page"),
    TRANSLATE("ترجمة الصفحة", "Translate Page")
}

/**
 * Result returned by Arna ai operations (Create, Generate, Edit, Summarize, Translate).
 */
data class BrowserAiResponse(
    val content: String,
    val isSampleMode: Boolean = true,
    val noticeText: String
)

/**
 * Abstraction for Arna ai capabilities inside the ARNA browser.
 * Backed by PlaceholderBrowserAiRepository until Firebase AI Logic is enabled.
 */
interface BrowserAiRepository {
    val isLiveAiEnabled: Boolean
    fun statusNotice(language: BrowserLanguage): String

    suspend fun runUniversalTask(
        mode: BrowserAiTaskMode,
        prompt: String,
        sourceContentToEdit: String = "",
        pageTitle: String,
        pageUrl: String,
        pageExcerpt: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): BrowserAiResponse

    suspend fun askAssistant(
        prompt: String,
        pageTitle: String,
        pageUrl: String,
        pageExcerpt: String,
        language: BrowserLanguage = BrowserLanguage.ARABIC
    ): BrowserAiResponse = runUniversalTask(
        mode = BrowserAiTaskMode.CREATE,
        prompt = prompt,
        pageTitle = pageTitle,
        pageUrl = pageUrl,
        pageExcerpt = pageExcerpt,
        language = language
    )

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
            "Notice: Live Gemini AI features for Arna ai (creating, generating, and editing anything) require Firebase to be enabled. Outputs shown here are labeled sample responses."
        } else {
            "ملاحظة: تشغيل Arna ai المباشر (Gemini) لصنع وتوليد وتعديل أي شيء يتطلب تفعيل Firebase. النتائج المعروضة حالياً هي ردود نموذجية تجريبية."
        }
    }

    override suspend fun runUniversalTask(
        mode: BrowserAiTaskMode,
        prompt: String,
        sourceContentToEdit: String,
        pageTitle: String,
        pageUrl: String,
        pageExcerpt: String,
        language: BrowserLanguage
    ): BrowserAiResponse {
        delay(300)
        val notice = statusNotice(language)
        val effectiveSource = sourceContentToEdit.ifBlank { pageExcerpt }.trim().take(260)

        if (language == BrowserLanguage.ENGLISH) {
            val contextHint = if (pageUrl.isNotBlank() && pageUrl != "about:blank") {
                "Page context: \"$pageTitle\" ($pageUrl)\n"
            } else {
                ""
            }
            val header = when (mode) {
                BrowserAiTaskMode.CREATE -> "✨ [Arna ai Create Sample — Firebase Required]"
                BrowserAiTaskMode.GENERATE -> "🪄 [Arna ai Generate Sample — Firebase Required]"
                BrowserAiTaskMode.EDIT -> "✏️ [Arna ai Edit Sample — Firebase Required]"
                BrowserAiTaskMode.SUMMARIZE -> "📝 [Arna ai Summary Sample — Firebase Required]"
                BrowserAiTaskMode.TRANSLATE -> "🌐 [Arna ai Translate Sample — Firebase Required]"
            }
            return BrowserAiResponse(
                content = buildString {
                    appendLine(header)
                    appendLine()
                    append(contextHint)
                    appendLine("Mode: ${mode.titleEn}")
                    appendLine("Request: \"$prompt\"")
                    if (effectiveSource.isNotBlank()) {
                        appendLine("\nInput / Page snippet:")
                        appendLine("\"$effectiveSource\"")
                    }
                    appendLine()
                    when (mode) {
                        BrowserAiTaskMode.CREATE ->
                            appendLine("• Arna ai Creation Preview: Structured draft/blueprint ready for your request. Enable Firebase AI Logic to run live Gemini creation for code, articles, UI concepts, and full projects.")
                        BrowserAiTaskMode.GENERATE ->
                            appendLine("• Arna ai Generation Preview: Creative variations and structured output prepared. Enable Firebase AI Logic to generate real-time Gemini text, code, scripts, and designs.")
                        BrowserAiTaskMode.EDIT ->
                            appendLine("• Arna ai Edit Preview: Ready to rewrite, fix, refactor, or transform your provided text/code. Enable Firebase AI Logic for live Gemini editing.")
                        else ->
                            appendLine("• Enable Firebase AI Logic to run live Gemini analysis for this page.")
                    }
                },
                isSampleMode = true,
                noticeText = notice
            )
        }

        val contextHint = if (pageUrl.isNotBlank() && pageUrl != "about:blank") {
            "سياق الصفحة الحالية: \"$pageTitle\" ($pageUrl)\n"
        } else {
            ""
        }
        val header = when (mode) {
            BrowserAiTaskMode.CREATE -> "✨ [رد Arna ai نموذجي (الإنشاء) — يتطلب تفعيل Firebase]"
            BrowserAiTaskMode.GENERATE -> "🪄 [رد Arna ai نموذجي (التوليد) — يتطلب تفعيل Firebase]"
            BrowserAiTaskMode.EDIT -> "✏️ [رد Arna ai نموذجي (التعديل) — يتطلب تفعيل Firebase]"
            BrowserAiTaskMode.SUMMARIZE -> "📝 [ملخص Arna ai نموذجي — يتطلب تفعيل Firebase]"
            BrowserAiTaskMode.TRANSLATE -> "🌐 [ترجمة Arna ai نموذجية — تتطلب تفعيل Firebase]"
        }
        return BrowserAiResponse(
            content = buildString {
                appendLine(header)
                appendLine()
                append(contextHint)
                appendLine("الوضع: ${mode.titleAr}")
                appendLine("طلبك: «$prompt»")
                if (effectiveSource.isNotBlank()) {
                    appendLine("\nالنص أو المقتطف المرفق:")
                    appendLine("«$effectiveSource»")
                }
                appendLine()
                when (mode) {
                    BrowserAiTaskMode.CREATE ->
                        appendLine("• معاينة الإنشاء في Arna ai: عند تفعيل Firebase، سيقوم Arna ai بصنع وإنشاء الأكواد والمقالات والتصاميم والخطط الكاملة مباشرة.")
                    BrowserAiTaskMode.GENERATE ->
                        appendLine("• معاينة التوليد في Arna ai: عند تفعيل Firebase، سيقوم Arna ai بتوليد النصوص والأفكار والسكريبتات والحلول المتقدمة فوراً.")
                    BrowserAiTaskMode.EDIT ->
                        appendLine("• معاينة التعديل في Arna ai: عند تفعيل Firebase، سيقوم Arna ai بتعديل وتحسين وإعادة صياغة أي نص أو كود أو محتوى صفحة بدقة.")
                    else ->
                        appendLine("• عند تفعيل Firebase، سيعمل مساعد Arna ai المباشر على معالجة محتوى الصفحة بدقة.")
                }
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
        delay(300)
        val notice = statusNotice(language)
        val cleaned = pageText.trim().replace(Regex("\\s+"), " ").take(450)
        if (language == BrowserLanguage.ENGLISH) {
            return BrowserAiResponse(
                content = buildString {
                    appendLine("🌐 [Arna ai Sample Translation to: $targetLanguage — Firebase Required]")
                    appendLine("Title: $pageTitle")
                    appendLine()
                    if (cleaned.isNotBlank()) {
                        appendLine("Extracted page text:")
                        appendLine(cleaned)
                        appendLine()
                    }
                    appendLine("• Full live webpage translation runs via Arna ai + Gemini once Firebase is enabled in project settings.")
                },
                isSampleMode = true,
                noticeText = notice
            )
        }
        return BrowserAiResponse(
            content = buildString {
                appendLine("🌐 [ترجمة Arna ai نموذجية إلى: $targetLanguage — تتطلب تفعيل Firebase]")
                appendLine("العنوان: $pageTitle")
                appendLine()
                if (cleaned.isNotBlank()) {
                    appendLine("النص المستخرج من الصفحة:")
                    appendLine(cleaned)
                    appendLine()
                }
                appendLine("• الترجمة الكاملة الفورية لصفحات الويب تعمل عبر Arna ai عند تفعيل Firebase في إعدادات المشروع.")
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
        delay(300)
        val notice = statusNotice(language)
        val sentences = pageText.trim()
            .split(Regex("[.،؛\n]+"))
            .map { it.trim() }
            .filter { it.length > 18 }
            .take(4)

        if (language == BrowserLanguage.ENGLISH) {
            return BrowserAiResponse(
                content = buildString {
                    appendLine("📝 [Arna ai Sample Summary — Firebase Required]")
                    appendLine("Title: $pageTitle")
                    appendLine()
                    if (sentences.isNotEmpty()) {
                        appendLine("Key extracted highlights:")
                        sentences.forEachIndexed { idx, line ->
                            appendLine("${idx + 1}. ${line.take(140)}")
                        }
                    } else {
                        appendLine("Open any article or text-rich webpage to summarize its key points, and enable Firebase for full Arna ai Gemini summaries.")
                    }
                },
                isSampleMode = true,
                noticeText = notice
            )
        }

        return BrowserAiResponse(
            content = buildString {
                appendLine("📝 [ملخص Arna ai نموذجي — يتطلب تفعيل Firebase]")
                appendLine("العنوان: $pageTitle")
                appendLine()
                if (sentences.isNotEmpty()) {
                    appendLine("أبرز النقاط المستخلصة محلياً من الصفحة:")
                    sentences.forEachIndexed { idx, line ->
                        appendLine("${idx + 1}. ${line.take(140)}")
                    }
                } else {
                    appendLine("افتح أي مقال أو صفحة ويب تحتوي على نصوص لاستخلاص ملخصها، ويمكن ربط Arna ai الكامل عبر Firebase.")
                }
            },
            isSampleMode = true,
            noticeText = notice
        )
    }
}
