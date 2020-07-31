package com.mobile.praaktishockey.ui.login.vm

import android.app.Application
import com.google.gson.Gson
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.AuthSeriviceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginFragmentViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }
    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }
    private val gson by lazy { Gson() }

    val loginEvent: LiveEvent<UserDTO?> = LiveEvent()
    val connectionErrorEvent: LiveEvent<Boolean> = LiveEvent()

    fun login(userName: String, password: String) {
        repo.login(userName, password)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                val temp = it.string()
                try {
                    val json = JSONObject(temp)
                    loginStorage.token = json.getString("token")
                    loginStorage.login = userName
                    loginStorage.password = password
                    loadProfile()
                } catch (e: Exception) {
                }
            }, ::onError)
    }

    fun loadProfile() {
        if (loginStorage.token.isNotEmpty())
            repo.getProfile()
                .doOnSubscribe { showHideEvent.postValue(true) }
                .doAfterTerminate { showHideEvent.postValue(false) }
                .subscribe({
                    loginStorage.setProfile(it)
                    loginEvent.postValue(it)
                }, ::onError)
        else loginEvent.postValue(null)
    }

    fun getProfile(): UserDTO? {
        return loginStorage.getProfile()
    }

    fun getLanguageObject(): LanguageItem? {
        val languageItem = loginEvent.value?.language
        if (languageItem != null) {
            val json = gson.toJson(loginEvent.value?.language)
            return gson.fromJson(json, LanguageItem::class.java)
        }
        return null
    }

    override fun onError(throwable: Throwable) {
        if (throwable is HttpException) {
            if (throwable.code() == 400 || throwable.code() == 401)
                loginEvent.postValue(null)
        } else if (throwable is UnknownHostException ||
            throwable is SocketTimeoutException
        ) {
            connectionErrorEvent.postValue(true)
        }
        super.onError(throwable)
    }
}