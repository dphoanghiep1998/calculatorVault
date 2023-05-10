package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
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
fun String.openWith(context: Context) {
    val file = File(this)
    val uri: Uri = Uri.fromFile(file)
    val fileName: String = file.name
    val intent = Intent(Intent.ACTION_VIEW)
    val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    context.grantUriPermission(context.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

    when {
        fileName.endsWith(".doc") || fileName.endsWith(".docx") -> {
            // Word document
            intent.setDataAndType(uri, "application/msword")
        }

        fileName.endsWith(".pdf") -> {
            // PDF file
            intent.setDataAndType(uri, "application/pdf")
        }

        fileName.endsWith(".ppt") || fileName.endsWith(".pptx") -> {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        }

        fileName.endsWith(".xls") || fileName.endsWith(".xlsx") -> {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        }

        fileName.endsWith(".zip") || fileName.endsWith(".rar") -> {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav")
        }

        fileName.endsWith(".rtf") -> {
            // RTF file
            intent.setDataAndType(uri, "application/rtf")
        }

        fileName.endsWith(".wav") || fileName.endsWith(".mp3") -> {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav")
        }

        fileName.endsWith(".gif") -> {
            // GIF file
            intent.setDataAndType(uri, "image/gif")
        }

        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") -> {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg")
        }

        fileName.endsWith(".txt") -> {
            // Text file
            intent.setDataAndType(uri, "text/plain")
        }

        fileName.endsWith(".3gp") || fileName.endsWith(".mpg") || fileName.endsWith(".mpeg") || fileName.endsWith(
            ".mpe"
        )
                || fileName.endsWith(".mp4") || fileName.endsWith(".avi") -> {
            // Video files
            intent.setDataAndType(uri, "video/*")
        }

        fileName.endsWith(".apk") -> {
            // APK file
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }

        else -> {
            // Use default type for unknown extensions
            intent.setDataAndType(uri, "*/*")
        }
    }
    intent.data = contentUri
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(intent, "Complete action using"))


}