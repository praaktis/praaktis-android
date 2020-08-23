package com.mobile.gympraaktis.ui.login.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage

class ForgotPasswordViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }
    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    val forgotPasswordEvent: LiveEvent<Boolean> = LiveEvent()

    fun forgotPassword(email: String) {
        repo.forgotPassword(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                forgotPasswordEvent.postValue(true)
            }, ::onError)

    }

}