package com.mobile.praaktishockey.ui.timeline.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TimelineFragmentViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val timelineDataEvent: LiveEvent<TimelineDTO> = LiveEvent()

    fun getTimelineData() {
        userService.getTimelineData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                timelineDataEvent.value = it
                settingsStorage.setTimeline(it)
            }, ::onError)
    }

    override fun onError(throwable: Throwable) {
        if (throwable is UnknownHostException
            || throwable is SocketTimeoutException
        ) {
            if (settingsStorage.getTimeline() != null) {
                timelineDataEvent.postValue(settingsStorage.getTimeline())
            }
        }
        super.onError(throwable)
    }
}