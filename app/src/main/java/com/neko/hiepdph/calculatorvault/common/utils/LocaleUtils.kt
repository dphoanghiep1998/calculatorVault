package com.neko.hiepdph.calculatorvault.common.utils

import android.annotation.SuppressLint
import com.neko.hiepdph.calculatorvault.R

import java.util.*


private object LocaleUtils {
    @SuppressLint("ConstantLocale")
    val defaultLocale: Locale = Locale.getDefault()
    val countryCodes: Set<String> = Locale.getISOCountries().toSet()
    val availableLocales: List<Locale> =
        Locale.getAvailableLocales().filter { countryCodes.contains(it.country) }

    @SuppressLint("ConstantLocale")

    val supportedLocales: MutableList<Locale> = mutableListOf(
        ENGLISH,
        FRENCH,
        INDIA,
        JAPANESE,
        BRAZIL,
        VIETNAM,
        KOREAN,
        TURKEY,
        SPAIN,
        ITALIA,
        GERMAN,
    )

    val supportLanguages: MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(R.string.ENGLISH, R.drawable.ic_language_english),
        Pair(R.string.FRENCH, R.drawable.ic_language_french),
        Pair(R.string.INDIA, R.drawable.ic_language_hindi),
        Pair(R.string.JAPANESE, R.drawable.ic_language_japan),
        Pair(R.string.BRAZIL, R.drawable.ic_language_brazin),
        Pair(R.string.VIETNAM, R.drawable.ic_language_vietnam),
        Pair(R.string.KOREAN, R.drawable.ic_language_korea),
        Pair(R.string.TURKEY, R.drawable.ic_language_turkey),
        Pair(R.string.SPAIN, R.drawable.ic_language_spain),
        Pair(R.string.ITALIA, R.drawable.ic_language_italia),
        Pair(R.string.GERMAN, R.drawable.ic_language_german)
    )
}

val VIETNAM = Locale("vi")
val ITALIA = Locale("it")
val ENGLISH = Locale("en")
val JAPANESE = Locale("ja")
val KOREAN = Locale("ko")
val FRENCH = Locale("fr")
val GERMAN = Locale("de")
val BRAZIL = Locale("pt")
val SPAIN = Locale("es")
val INDIA = Locale("hi")
val TURKEY = Locale("tr")
val INDONESIA = Locale("in")
val ADS = Locale("ADS_LANGUAGE")


private const val SEPARATOR: String = "_"


fun supportedLanguages(): MutableList<Locale> = LocaleUtils.supportedLocales
fun supportDisplayLang(): MutableList<Pair<Int, Int>> = LocaleUtils.supportLanguages

fun defaultCountryCode(): String = LocaleUtils.defaultLocale.country

fun defaultLanguageTag(): String = toLanguageTag(LocaleUtils.defaultLocale)

fun toLanguageTag(locale: Locale): String = locale.language + SEPARATOR + locale.country

