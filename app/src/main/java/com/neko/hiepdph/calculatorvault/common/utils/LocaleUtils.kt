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
    val countriesLocales: SortedMap<String, Locale> =
        availableLocales.associateBy { it.country.toCapitalize(Locale.getDefault()) }.toSortedMap()
    val supportedLocales: MutableList<Locale> = mutableListOf(
        ENGLISH,
        FRENCH,
        INDIA,
        INDONESIA,
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
        Pair(R.string.ENGLISH, R.drawable.ic_launcher_background),
        Pair(R.string.FRENCH, R.drawable.ic_launcher_background),
        Pair(R.string.INDIA, R.drawable.ic_launcher_background),
        Pair(R.string.INDONESIA, R.drawable.ic_launcher_background),
        Pair(R.string.JAPANESE, R.drawable.ic_launcher_background),
        Pair(R.string.BRAZIL, R.drawable.ic_launcher_background),
        Pair(R.string.VIETNAM, R.drawable.ic_launcher_background),
        Pair(R.string.KOREAN, R.drawable.ic_launcher_background),
        Pair(R.string.TURKEY, R.drawable.ic_launcher_background),
        Pair(R.string.SPAIN, R.drawable.ic_launcher_background),
        Pair(R.string.ITALIA, R.drawable.ic_launcher_background),
        Pair(R.string.GERMAN, R.drawable.ic_launcher_background)
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

fun findByCountryCode(countryCode: String): Locale =
    LocaleUtils.availableLocales.find { countryCode.toCapitalize(Locale.getDefault()) == it.country }
        ?: LocaleUtils.defaultLocale

fun allCountries(): List<Locale> = LocaleUtils.countriesLocales.values.toList()

fun findByLanguageTag(languageTag: String): Locale {
    val languageTagPredicate: (Locale) -> Boolean = {
        val locale: Locale = fromLanguageTag(languageTag)
        it.language == locale.language
    }
    return LocaleUtils.supportedLocales.find(languageTagPredicate) ?: LocaleUtils.defaultLocale
}

fun supportedLanguages(): MutableList<Locale> = LocaleUtils.supportedLocales
fun supportDisplayLang(): MutableList<Pair<Int, Int>> = LocaleUtils.supportLanguages

fun defaultCountryCode(): String = LocaleUtils.defaultLocale.country

fun defaultLanguageTag(): String = toLanguageTag(LocaleUtils.defaultLocale)

fun toLanguageTag(locale: Locale): String = locale.language + SEPARATOR + locale.country

private fun fromLanguageTag(languageTag: String): Locale {
    val codes: Array<String> = languageTag.split(SEPARATOR).toTypedArray()
    return when (codes.size) {
        1 -> Locale(codes[0])
        2 -> Locale(codes[0], codes[1].toCapitalize(Locale.getDefault()))
        else -> LocaleUtils.defaultLocale
    }
}
