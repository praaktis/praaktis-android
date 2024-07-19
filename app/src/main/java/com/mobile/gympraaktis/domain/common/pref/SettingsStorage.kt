package com.mobile.gympraaktis.domain.common.pref

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.gympraaktis.domain.entities.*
import java.util.*

object SettingsStorage {
    @SuppressLint("StaticFieldLeak")
    lateinit var instance: LoginPreferences

    fun initWith(context: Context) {
        instance = LoginPreferences(context.applicationContext)
    }

    fun setLocale(context: Context): Context {
        val locale = Locale(instance.getLanguage())
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    val supportLanguages: List<LanguageItem> = listOf(
        LanguageItem(1, "English", "en"),
        LanguageItem(2, "French", "fr"),
        LanguageItem(3, "Uzbek", "uz"),
        LanguageItem(4, "Russian", "ru")
    )

}

class LoginPreferences(context: Context) : BaseSettings(context), LoginSettings {
    var token by prefString()
    var fcmToken by prefString()
    var isSentFcmToken by prefBoolean()
    var showedIntroPage by prefBoolean()
    var lang by prefString()
    var login by prefString()
    var password by prefString()
    var cameraMode by prefBoolean()

    override fun cameraMode(): Boolean = cameraMode

    override fun isLoggedIn(): Boolean = token.isNotEmpty()

    override fun logout() {
        showedIntroPage = false
        token = ""
        preferences.edit().clear().apply()
    }

    override fun token(): String = token

    override fun unsafeToken() = token.takeIf { it.isNotEmpty() }

    override fun isShowedIntroPage(): Boolean = showedIntroPage

    override fun getLanguage(): String {
        if (lang.isNotEmpty()) return lang
        lang = "uz"
        return "uz"
    }

    override fun getProfile(): UserDTO? {
        val temp = preferences.getString("profile", null) ?: return null
        return Gson().fromJson(temp, UserDTO::class.java)
    }

    override fun setProfile(userDTO: UserDTO) {
        preferences.edit().putString("profile", Gson().toJson(userDTO)).commit()
    }

    override fun getChallenges(): List<ChallengeDTO>? {
        if (preferences.getString("challenges", null) != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<ChallengeDTO>>() {}.type
            val newList = gson.fromJson<List<ChallengeDTO>>(
                preferences.getString("challenges", null),
                listType
            )
            return newList
        }
        return null
    }

    override fun setChallenges(challenges: List<ChallengeDTO>) {
        preferences.edit().putString("challenges", Gson().toJson(challenges)).commit()
    }

    override fun setTimelineDetails(timelineDetails: List<DetailScoreDTO>, attemptId: Int) {
        preferences.edit().putString("timelineDetails_$attemptId", Gson().toJson(timelineDetails))
            .commit()
    }

    override fun getTimelineDetails(attemptId: Int): List<DetailScoreDTO>? {
        if (preferences.getString("timelineDetails_$attemptId", null) != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<DetailScoreDTO>>() {}.type
            return gson.fromJson<List<DetailScoreDTO>>(
                preferences.getString(
                    "timelineDetails_$attemptId",
                    null
                ), listType
            )
        }
        return null
    }

    override fun setComparison(comparisonDTO: ComparisonDTO) {
        preferences.edit().putString("comparison", Gson().toJson(comparisonDTO)).commit()
    }

    override fun getComparison(): ComparisonDTO? {
        if (preferences.getString("comparison", null) != null) {
            return Gson().fromJson(
                preferences.getString("comparison", ""),
                ComparisonDTO::class.java
            )
        }
        return null
    }


    override fun getSelectedPlayerId(): Long {
        return preferences.getLong("SELECTED_PLAYER", -1L)
    }

    override fun setSelectedPlayerId(playerId: Long) {
        preferences.edit().putLong("SELECTED_PLAYER", playerId).commit()
    }
}