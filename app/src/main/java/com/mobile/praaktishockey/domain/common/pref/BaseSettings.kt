package com.mobile.praaktishockey.domain.common.pref

import android.content.Context
import android.content.SharedPreferences

open class BaseSettings(val context: Context) {
    val preferences: SharedPreferences by lazy { context.getSharedPreferences("preferencesHockey.login", Context.MODE_PRIVATE) }
    fun prefString() = PrefStringDelegate(preferences)
    fun prefLong() = PrefLongDelegate(preferences)
    fun prefInt(defVal: Int = -1) = PrefIntDelegate(preferences, defVal)
    fun prefBoolean() = PrefBooleanDelegate(preferences)
}