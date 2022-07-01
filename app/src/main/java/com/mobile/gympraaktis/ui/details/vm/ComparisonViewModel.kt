package com.mobile.gympraaktis.ui.details.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.ComparisonDTO

class ComparisonViewModel(application: Application) : BaseViewModel(application) {

    private val userRepository : UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    val meVsOthersEvent: LiveEvent<ComparisonDTO> = LiveEvent()

    fun getMeVsOthers(playerId: Long) {
        userRepository.getComparison(playerId)
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