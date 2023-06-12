package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
}
