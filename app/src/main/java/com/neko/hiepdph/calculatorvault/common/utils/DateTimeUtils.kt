package com.neko.hiepdph.calculatorvault.common.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun getDateConverted(date: Date?): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return formatter.format(date).toString()
    }
    fun getDateConverted(time:Long):String{
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(time).toString()
    }

    fun getDateConvertedToResult(time: Long): String {
        val formatter = SimpleDateFormat("MMM dd, HH:mm")
        return formatter.format(time).toString()
    }

    fun getDateConvertedToResult(time: Long, locale: Locale): String {
        val formatter = SimpleDateFormat("MMM dd, HH:mm", locale)
        return formatter.format(time).toString()
    }


    fun convertToDateMonth(date: Date): String {
        val cCalendar = Calendar.getInstance()
        val dCalendar = Calendar.getInstance()
        dCalendar.timeInMillis = date.time
        if (cCalendar.get(Calendar.DATE) == dCalendar.get(Calendar.DATE)) {
            return "Today"
        }
        val formatter = SimpleDateFormat("MM-dd", Locale.ENGLISH)
        return formatter.format(date).toString()
    }

    fun convertToDateMonth(time: Long): String {
        val formatter = SimpleDateFormat("dd/MMM", Locale.US)
        return formatter.format(time).toString()
    }

    fun convertTimeStringToCalendar(time: String): Date {
        try {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
            return simpleDateFormat.parse(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Date()
    }

    fun convertTimeStringToTimestamp(time: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
            return simpleDateFormat.parse(time).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Date().time

    }

    fun convertDateStringToCalendar(time: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.parse(time).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Date().time
    }

    fun convertTimestampToString(time: Long): String {
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            var hour = ""
            var minute = ""
            hour = if (calendar[Calendar.HOUR_OF_DAY] < 10) {
                "0${calendar[Calendar.HOUR_OF_DAY]}"
            } else {
                "${calendar[Calendar.HOUR_OF_DAY]}"
            }
            minute = if (calendar[Calendar.MINUTE] < 10) {
                "0${calendar[Calendar.MINUTE]}"
            } else {
                "${calendar[Calendar.MINUTE]}"

            }
            return "${hour}:${minute}"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertToLong(date: String, time: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            Log.d("TAG", "${date} ${time}")
            return simpleDateFormat.parse("${date} ${time}").time
        } catch (e: Exception) {
            Log.d("TAG", "convertToLongLmao: ")
            e.printStackTrace()
        }
        return Date().time

    }
}