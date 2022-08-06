package com.mobile.gympraaktis.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.gympraaktis.domain.entities.DetailPoint
import com.mobile.gympraaktis.domain.entities.DetailResult
import com.praaktis.exerciseengine.Engine.Measurement

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

class DetailPointListConverter {

    @TypeConverter
    fun listToJson(value: List<DetailPoint>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): List<DetailPoint>? {
        return Gson().fromJson(value, object : TypeToken<List<DetailPoint>>() {}.type)
    }

}

class MeasureResultListConverter {

    @TypeConverter
    fun listToJson(value: List<Measurement>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): List<Measurement>? {
        return Gson().fromJson(value, object : TypeToken<List<Measurement>>() {}.type)
    }

}