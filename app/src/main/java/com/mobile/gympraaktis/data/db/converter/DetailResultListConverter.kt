package com.mobile.gympraaktis.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.gympraaktis.domain.entities.DetailResult

class DetailResultListConverter {

    @TypeConverter
    fun listToJson(value: List<DetailResult>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): List<DetailResult>? {
        return Gson().fromJson(value, object : TypeToken<List<DetailResult>>() {}.type)
    }

}