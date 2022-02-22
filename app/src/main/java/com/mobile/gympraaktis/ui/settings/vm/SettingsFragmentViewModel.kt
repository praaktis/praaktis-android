package com.mobile.gympraaktis.ui.settings.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.UserDTO
import org.json.JSONObject

class SettingsFragmentViewModel(app: Application) : BaseViewModel(app) {

    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    fun getLanguage(): String = settingsStorage.getLanguage()

    fun saveLanguage(language: String) {
        settingsStorage.lang = language
    }

    val updateProfileLanguageEvent: LiveEvent<String> = LiveEvent()

    fun updateProfileLanguage(localeCode: String) {
        val languageKey = when (localeCode) {
            "en" -> 1
            "fr" -> 2
            else -> 1
        }

        repo.updateProfile(UserDTO(language = languageKey), null)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                val json = JSONObject(it.string())
                val message = json.getString("message")
                updateProfileLanguageEvent.postValue(localeCode)
            }, ::onError)
    }

}