package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.PowerManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import java.util.*

fun Context.createContext(newLocale: Locale): Context = if (buildMinVersionN()) {
    createContextAndroidN(newLocale)
} else {
    createContextLegacy(newLocale)
}

private fun Context.createContextAndroidN(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(newLocale)
    return createConfigurationContext(configuration)
}
fun Context.openLink(strUri: String?) {
    try {
        val uri = Uri.parse(strUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Context.toDp(sizeInDp: Int): Int {
    val scale: Float = this.resources.displayMetrics.density
    return (sizeInDp * scale + 0.5f).toInt()
}


private fun Context.createContextLegacy(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.locale = newLocale
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return this
}

fun Context.isScreenOn() = (getSystemService(Context.POWER_SERVICE) as PowerManager).isScreenOn


@ColorInt
fun Context.compatColor(@ColorRes id: Int): Int = if (buildMinVersionM()) {
    getColor(id)
} else {
    ContextCompat.getColor(this, id)
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

