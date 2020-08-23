package com.mobile.gympraaktis.ui.challenge.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.DetailScoreDTO
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DetailAnalysisFragmentViewModel(application: Application) : BaseViewModel(application) {

    val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    val detailResultEvent: LiveEvent<List<DetailScoreDTO>> = LiveEvent()

    var attemptId: Int? = null

    fun getDetailResult(attemptId: Int) {
        this.attemptId = attemptId
        userRepository.getDetailResult(attemptId)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                detailResultEvent.postValue(it)
                settingsStorage.setTimelineDetails(it, attemptId)
            }, ::onError)
    }

    override fun onError(throwable: Throwable) {
        if (throwable is UnknownHostException
            || throwable is SocketTimeoutException
        ) {
            attemptId?.let {
                if (settingsStorage.getTimelineDetails(it) != null) {
                    detailResultEvent.postValue(settingsStorage.getTimelineDetails(it))
                }
            }
        }
        super.onError(throwable)
    }

}