package com.mobile.praaktishockey.ui.details.vm

import android.app.Application
import com.mobile.praaktishockey.domain.entities.MeVsOthersDTO
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.LoginSettings
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.ComparisonDTO

class ComparisonViewModel(application: Application) : BaseViewModel(application) {

    private val userRepository : UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    val meVsOthersEvent: LiveEvent<ComparisonDTO> = LiveEvent()

    fun getMeVsOthers() {
        userRepository.getComparison()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                settingsStorage.setComparison(it)
                meVsOthersEvent.postValue(it)
            }, ::onError)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        settingsStorage.getComparison()?.let { meVsOthersEvent.postValue(it) }
    }
}