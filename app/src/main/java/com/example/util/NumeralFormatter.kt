package com.example.util

object NumeralFormatter {
    private val westernToArabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val arabicToWesternDigits = mapOf(
        '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
        '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
    )

    fun format(text: String, useArabicNumerals: Boolean): String {
        if (text.isEmpty()) return text
        val sb = StringBuilder(text.length)
        for (c in text) {
            if (useArabicNumerals) {
                if (c in '0'..'9') {
                    sb.append(westernToArabicDigits[c - '0'])
                } else {
                    sb.append(c)
                }
            } else {
                val western = arabicToWesternDigits[c]
                if (western != null) {
                    sb.append(western)
                } else {
                    sb.append(c)
                }
            }
        }
        return sb.toString()
    }

    fun formatNumber(number: Int, useArabicNumerals: Boolean): String {
        return format(number.toString(), useArabicNumerals)
    }

    fun formatNumber(number: Long, useArabicNumerals: Boolean): String {
        return format(number.toString(), useArabicNumerals)
    }
}

fun String.formatNumerals(useArabic: Boolean): String = NumeralFormatter.format(this, useArabic)
fun Int.formatNumerals(useArabic: Boolean): String = NumeralFormatter.formatNumber(this, useArabic)
fun Long.formatNumerals(useArabic: Boolean): String = NumeralFormatter.formatNumber(this, useArabic)
