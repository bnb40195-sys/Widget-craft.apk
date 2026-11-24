package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.sin

data class WallpaperStylePreset(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val promptHint: String,
    val startColor: Int,
    val midColor: Int,
    val endColor: Int,
    val accentColor: Int,
    val recommendedDim: Float
)

data class GeneratedWallpaperResult(
    val imageFilePath: String,
    val styleTitle: String,
    val promptUsed: String,
    val recommendedDim: Float,
    val isSampleMode: Boolean,
    val noticeText: String
)

interface WallpaperAiRepository {
    val isLiveAiEnabled: Boolean
    fun statusNotice(): String
    suspend fun generateWallpaper(
        context: Context,
        prompt: String,
        style: WallpaperStylePreset
    ): GeneratedWallpaperResult
}

val wallpaperAiPresets = listOf(
    WallpaperStylePreset(
        id = "islamic_gold",
        titleAr = "زخرفة إسلامية مذهبة",
        titleEn = "Islamic Gold Geometry",
        promptHint = "زخرفة هندسية إسلامية فاخرة باللونين الكحلي والذهبي",
        startColor = 0xFF0A1024.toInt(),
        midColor = 0xFF172554.toInt(),
        endColor = 0xFF0F172A.toInt(),
        accentColor = 0xFFF59E0B.toInt(),
        recommendedDim = 0.25f
    ),
    WallpaperStylePreset(
        id = "cosmos_neon",
        titleAr = "سديم كوني نيون",
        titleEn = "Neon Cosmos Nebula",
        promptHint = "سديم كوني متوهج بألوان البنفسجي والسماوي بدون أي كائنات",
        startColor = 0xFF0F0720.toInt(),
        midColor = 0xFF31105E.toInt(),
        endColor = 0xFF091B3A.toInt(),
        accentColor = 0xFF22D3EE.toInt(),
        recommendedDim = 0.22f
    ),
    WallpaperStylePreset(
        id = "desert_dusk",
        titleAr = "شفق صحراوي هادئ",
        titleEn = "Calm Desert Horizon",
        promptHint = "كثبان رملية صافية وقت الغروب بألوان دافئة هادئة",
        startColor = 0xFF2C0B05.toInt(),
        midColor = 0xFF7C2D12.toInt(),
        endColor = 0xFF1F1016.toInt(),
        accentColor = 0xFFFB923C.toInt(),
        recommendedDim = 0.25f
    ),
    WallpaperStylePreset(
        id = "emerald_aurora",
        titleAr = "زمرد الأورورا الملكي",
        titleEn = "Royal Emerald Aurora",
        promptHint = "شفق قطبي زمردي نقي ومموج مع خطوط ضوئية هادئة",
        startColor = 0xFF021F19.toInt(),
        midColor = 0xFF064E3B.toInt(),
        endColor = 0xFF071E2E.toInt(),
        accentColor = 0xFF34D399.toInt(),
        recommendedDim = 0.20f
    ),
    WallpaperStylePreset(
        id = "oled_prism",
        titleAr = "أسود OLED هندسي",
        titleEn = "OLED Geometric Prism",
        promptHint = "تصميم داكن نقي OLED مع خطوط هندسية فضية خفيفة",
        startColor = 0xFF000000.toInt(),
        midColor = 0xFF0A0D14.toInt(),
        endColor = 0xFF111827.toInt(),
        accentColor = 0xFF38BDF8.toInt(),
        recommendedDim = 0.10f
    )
)

class PlaceholderWallpaperAiRepository : WallpaperAiRepository {
    override val isLiveAiEnabled: Boolean = false

    override fun statusNotice(): String =
        "ملاحظة: التوليد السحابي للخلفيات عبر Gemini (Nano Banana) يتطلب تفعيل Firebase. تم إنشاء خلفية هندسية نموذجية عالية الدقة خالية من ذوات الأرواح."

    override suspend fun generateWallpaper(
        context: Context,
        prompt: String,
        style: WallpaperStylePreset
    ): GeneratedWallpaperResult = withContext(Dispatchers.IO) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Base multi-stop gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                intArrayOf(style.startColor, style.midColor, style.endColor),
                floatArrayOf(0f, 0.52f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Atmospheric radial glow driven by prompt hash so different prompts produce distinct art
        val seed = (prompt.trim().hashCode() xor style.id.hashCode())
        val cx1 = width * (0.25f + ((seed and 0x3F) / 128f))
        val cy1 = height * (0.28f + (((seed shr 6) and 0x3F) / 140f))
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx1,
                cy1,
                width * 0.72f,
                intArrayOf((style.accentColor and 0x00FFFFFF) or 0x55000000, 0x00000000),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx1, cy1, width * 0.72f, glowPaint)

        // Crisp non-figurative geometric / Islamic star lattice overlay
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = (style.accentColor and 0x00FFFFFF) or 0x38000000
            setStyle(Paint.Style.STROKE)
            strokeWidth = 2.6f
        }
        val centerX = width / 2f
        val centerY = height * 0.44f
        val rings = listOf(140f, 230f, 340f, 460f)
        rings.forEachIndexed { idx, radius ->
            canvas.drawCircle(centerX, centerY, radius, linePaint)
            val points = 8 + (idx % 2) * 4
            val polygon = Path()
            for (i in 0..points) {
                val angle = Math.toRadians((360.0 / points) * i + idx * 15.0)
                val px = centerX + (radius * cos(angle)).toFloat()
                val py = centerY + (radius * sin(angle)).toFloat()
                if (i == 0) polygon.moveTo(px, py) else polygon.lineTo(px, py)
            }
            polygon.close()
            canvas.drawPath(polygon, linePaint)
        }

        // Horizon wave curves near bottom
        val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = (style.accentColor and 0x00FFFFFF) or 0x26000000
            setStyle(Paint.Style.FILL)
        }
        val wave = Path().apply {
            moveTo(0f, height * 0.78f)
            cubicTo(
                width * 0.3f, height * 0.71f,
                width * 0.68f, height * 0.87f,
                width.toFloat(), height * 0.76f
            )
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(wave, wavePaint)

        val outDir = File(context.filesDir, "ai_wallpapers").apply { mkdirs() }
        val outFile = File(outDir, "wallpaper_${style.id}_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos)
        }
        bitmap.recycle()

        GeneratedWallpaperResult(
            imageFilePath = outFile.absolutePath,
            styleTitle = style.titleAr,
            promptUsed = prompt.ifBlank { style.promptHint },
            recommendedDim = style.recommendedDim,
            isSampleMode = true,
            noticeText = statusNotice()
        )
    }
}
