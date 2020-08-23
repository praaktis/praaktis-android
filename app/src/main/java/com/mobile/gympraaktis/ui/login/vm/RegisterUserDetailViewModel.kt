package com.mobile.gympraaktis.ui.login.vm

import android.app.Application
import com.google.gson.Gson
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.LanguageItem
import com.mobile.gympraaktis.domain.entities.UserDTO
import org.json.JSONObject

class RegisterUserDetailViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }
    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    val updateProfileEvent: LiveEvent<String> = LiveEvent()
    val getAcceptTermsEvent: LiveEvent<String> = LiveEvent()
    val profileInfoEvent: LiveEvent<UserDTO> = LiveEvent()
    val acceptTermsEvent: LiveEvent<Boolean> = LiveEvent()

    fun updateProfile(user: UserDTO) {
        repo.updateProfile(user)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                val json = JSONObject(it.string())
                val message = json.getString("message")
                updateProfileEvent.postValue(message)
            }, ::onError)
    }

    fun acceptTerms() {
        repo.acceptTerms()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                acceptTermsEvent.postValue(true)
            }, ::onError)
    }

    fun getTermsConditions() {
        repo.getAcceptTerms()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                var temp = it.string().replace("\"", "")
                /*if (!temp.startsWith("http"))
                    temp = "https://$temp"*/
                getAcceptTermsEvent.postValue(temp)
            }, ::onError)
    }

    fun loadProfile() {
        repo.getProfile()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                profileInfoEvent.postValue(it)
            }, ::onError)
    }

    fun getLanguageObject(): LanguageItem? {
        val languageItem = profileInfoEvent.value?.language
        if (languageItem != null) {
            val json = Gson().toJson(profileInfoEvent.value?.language)
            return Gson().fromJson(json, LanguageItem::class.java)
        }
        return null
    }
}