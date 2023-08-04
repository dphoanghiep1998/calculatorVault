package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.neko.hiepdph.calculatorvault.BuildConfig
import com.neko.hiepdph.calculatorvault.common.Constant
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
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

fun String.openWith(context: Context, type: String, realType: String?) {
    Log.d("TAG", "openWith: " + this)
    val file = File(this)
    val uri: Uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_VIEW)

    val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    context.grantUriPermission(
        BuildConfig.APPLICATION_ID, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
    when (type) {
        Constant.TYPE_FILE -> {
            when (realType) {
                // Word document
                Constant.TYPE_WORD, Constant.TYPE_WORDX -> intent.setDataAndType(
                    uri, "application/msword"
                )

                Constant.TYPE_PDF -> intent.setDataAndType(uri, "application/pdf")
                Constant.TYPE_PPT, Constant.TYPE_PPTX -> intent.setDataAndType(
                    uri, "application/vnd.ms-powerpoint"
                )

                Constant.TYPE_EXCEL, Constant.TYPE_CSV -> intent.setDataAndType(
                    uri, "application/vnd.ms-excel"
                )

                Constant.TYPE_ZIP -> intent.setDataAndType(
                    uri, "application/zip"
                )

                Constant.TYPE_RAR -> intent.setDataAndType(
                    uri, "application/rar"
                )

                Constant.TYPE_TEXT -> intent.setDataAndType(uri, "text/plain")
            }
        }

        Constant.TYPE_AUDIOS -> intent.setDataAndType(
            uri, "audio/*"
        )

        Constant.TYPE_VIDEOS -> {
            intent.setDataAndType(
                uri, "video/*"
            )
        }

        else -> {
            // Use default type for unknown extensions
            intent.setDataAndType(uri, "*/*")
        }
    }
    intent.setDataAndType(uri, TypeOpen.getType(file))
    intent.data = contentUri
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(intent, "Complete action using"))


}