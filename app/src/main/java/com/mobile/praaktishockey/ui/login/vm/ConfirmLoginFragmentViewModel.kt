package com.mobile.praaktishockey.ui.login.vm

import android.app.Application
import com.google.gson.Gson
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.AuthSeriviceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import okhttp3.ResponseBody

class ConfirmLoginFragmentViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }

    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    val profileInfoEvent: LiveEvent<UserDTO> = LiveEvent()

    fun loadProfile() {
        repo.getProfile()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                profileInfoEvent.postValue(it)
            }, ::onError)
    }

    fun getProfile(): UserDTO? {
        return loginStorage.getProfile()
    }

    fun getLanguageObject(): LanguageItem? {
        val languageItem = profileInfoEvent.value?.language
        if (languageItem != null) {
            val json = Gson().toJson(profileInfoEvent.value?.language)
            return Gson().fromJson(json, LanguageItem::class.java)
        }
        return null
    }

    val resendActivationEvent: LiveEvent<ResponseBody> = LiveEvent()

    fun resendActivation() {
        repo.resendActivation()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                resendActivationEvent.postValue(it)
            }, ::onError)
    }
}