package com.mobile.praaktishockey.ui.login.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.AuthSeriviceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import org.json.JSONObject

class RegisterViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }
    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    val createUserEvent: LiveEvent<Boolean> = LiveEvent()
    val loginEvent: LiveEvent<Boolean> = LiveEvent()

    fun createUser(email: String, password: String) {
        repo.createUser(email, password)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                login(email, password)
            }, ::onError)
    }

    fun login(userName: String, password: String) {
        repo.login(userName, password)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                val temp = it.string()
                val json = JSONObject(temp)
                loginStorage.token = json.getString("token")
                loginStorage.login = userName
                loginStorage.password = password
                loginEvent.postValue(true)
            }, ::onError)
    }

}