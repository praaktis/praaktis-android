package com.mobile.praaktishockey.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DoubleListConverter {

    @TypeConverter
    fun listToJson(value: List<Double>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): List<Double>? {
        return Gson().fromJson(value, object : TypeToken<List<Double>>() {}.type)
    }

}