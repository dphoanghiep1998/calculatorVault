package com.neko.hiepdph.calculatorvault.common.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

val String.Companion.EMPTY: String get() = ""
val String.Companion.SPACE_SEPARATOR: String get() = " "
const val SQRT_SYMBOL = "\u221A"
const val DIVIDE_SYMBOL = "\u00F7"
const val PI_SYMBOL = "\u03C0"
const val ARC_SIN = "sin⁻¹"
const val ARC_COS = "cos⁻¹"
const val ARC_TAN = "tan⁻¹"

fun String.specialTrim(): String =
    this.trim { it <= ' ' }.replace(" +".toRegex(), String.SPACE_SEPARATOR)

fun String.toCapitalize(locale: Locale): String =
    this.replaceFirstChar { word -> word.uppercase(locale) }

fun format(d: Float): Float {
    var bd: BigDecimal = BigDecimal.valueOf(d.toDouble())
    bd = bd.setScale(1, RoundingMode.HALF_EVEN)
    return bd.toFloat()
}

fun formatMg(d: Float): Float {
    return d.roundToInt().toFloat()
}

fun formatMgToText(value: Float): String {
    return value.roundToInt().toString()
}

fun roundOffDecimal(number: Double): Double {
    val number3digits: Double = (number * 1000.0).roundToInt() / 1000.0
    val number2digits: Double = (number3digits * 100.0).roundToInt() / 100.0
    return (number2digits * 10.0).roundToInt() / 10.0
}