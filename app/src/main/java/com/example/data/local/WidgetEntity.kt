package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetTheme
import com.example.data.model.WidgetType

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val tagline: String,
    val type: String,
    val theme: String,
    val customPrimaryColor: Long?,
    val customSecondaryColor: Long?,
    val customTextColor: Long?,
    val cornerRadiusDp: Int,
    val size: String,
    val showDate: Boolean,
    val showBattery: Boolean,
    val showSeconds: Boolean,
    val noteContent: String,
    val counterValue: Int,
    val counterStep: Int,
    val counterLabel: String,
    val quoteArabic: String,
    val quoteEnglish: String,
    val quoteAuthor: String,
    val cityName: String = "وهران (Oran)",
    val cityLatitude: Double = 35.6976,
    val cityLongitude: Double = -0.6337,
    val cityTimeZone: Double = 1.0,
    val temperatureCelsius: Int = 24,
    val weatherCondition: String = "مشمس ومعتدل",
    val analogStyle: String = "ALG_DARK",
    val hijriDayOffset: Int = 0,
    val quranSurahName: String = "سورة الفاتحة",
    val quranReciterName: String = "مشاري راشد العفاسي",
    val isActiveOnLauncher: Boolean,
    val isFavorite: Boolean,
    val source: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): WidgetConfig {
        val widgetType = WidgetType.values().find { it.name == type || it.id == type } ?: WidgetType.DIGITAL_CLOCK
        val widgetTheme = WidgetTheme.values().find { it.name == theme || it.id == theme } ?: WidgetTheme.CYBER_NEON
        val widgetSize = try { WidgetSize.valueOf(size) } catch (e: Exception) { WidgetSize.MEDIUM_4x2 }

        return WidgetConfig(
            id = id,
            name = name,
            tagline = tagline,
            type = widgetType,
            theme = widgetTheme,
            customPrimaryColor = customPrimaryColor,
            customSecondaryColor = customSecondaryColor,
            customTextColor = customTextColor,
            cornerRadiusDp = cornerRadiusDp,
            size = widgetSize,
            showDate = showDate,
            showBattery = showBattery,
            showSeconds = showSeconds,
            noteContent = noteContent,
            counterValue = counterValue,
            counterStep = counterStep,
            counterLabel = counterLabel,
            quoteArabic = quoteArabic,
            quoteEnglish = quoteEnglish,
            quoteAuthor = quoteAuthor,
            cityName = cityName,
            cityLatitude = cityLatitude,
            cityLongitude = cityLongitude,
            cityTimeZone = cityTimeZone,
            temperatureCelsius = temperatureCelsius,
            weatherCondition = weatherCondition,
            analogStyle = analogStyle,
            hijriDayOffset = hijriDayOffset,
            quranSurahName = quranSurahName,
            quranReciterName = quranReciterName,
            isActiveOnLauncher = isActiveOnLauncher,
            isFavorite = isFavorite,
            source = source,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(config: WidgetConfig): WidgetEntity {
            return WidgetEntity(
                id = config.id,
                name = config.name,
                tagline = config.tagline,
                type = config.type.name,
                theme = config.theme.name,
                customPrimaryColor = config.customPrimaryColor,
                customSecondaryColor = config.customSecondaryColor,
                customTextColor = config.customTextColor,
                cornerRadiusDp = config.cornerRadiusDp,
                size = config.size.name,
                showDate = config.showDate,
                showBattery = config.showBattery,
                showSeconds = config.showSeconds,
                noteContent = config.noteContent,
                counterValue = config.counterValue,
                counterStep = config.counterStep,
                counterLabel = config.counterLabel,
                quoteArabic = config.quoteArabic,
                quoteEnglish = config.quoteEnglish,
                quoteAuthor = config.quoteAuthor,
                cityName = config.cityName,
                cityLatitude = config.cityLatitude,
                cityLongitude = config.cityLongitude,
                cityTimeZone = config.cityTimeZone,
                temperatureCelsius = config.temperatureCelsius,
                weatherCondition = config.weatherCondition,
                analogStyle = config.analogStyle,
                hijriDayOffset = config.hijriDayOffset,
                quranSurahName = config.quranSurahName,
                quranReciterName = config.quranReciterName,
                isActiveOnLauncher = config.isActiveOnLauncher,
                isFavorite = config.isFavorite,
                source = config.source,
                createdAt = config.createdAt,
                updatedAt = config.updatedAt
            )
        }
    }
}
