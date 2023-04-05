package com.neko.hiepdph.calculatorvault.data.converter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
class Converter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toString(listUri: String): MutableList<String>? {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson<MutableList<String>>(listUri, listType)
    }

    @TypeConverter
    fun fromListString(listUri: MutableList<String>): String? {
        return Gson().toJson(listUri)
    }

}