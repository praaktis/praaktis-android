package com.mobile.praaktishockey.ui.main.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.DashboardDTO

class DashboardViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    val dashboardEvent: LiveEvent<DashboardDTO> = LiveEvent()

    fun getDashboardData() {
        userService.getDashboardData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                dashboardEvent.postValue(it)
                if (it != null)
                    settingsStorage.setDashboard(it)
            }, ::onError)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        settingsStorage.getDashboard()?.let { dashboardEvent.postValue(it) }
    }

}