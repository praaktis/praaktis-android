package com.mobile.gympraaktis.ui.challenge.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ResultChallengeFragmentViewModel(application: Application) : BaseViewModel(application) {

    private val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    fun storeResult(
        challengeItem: ChallengeDTO,
        points: Int? = null,
        score: Float,
        credits: Float? = null,
        detailResults: List<DetailResult>,
        videoId: String? = null
    ) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val request = StoreResultDTO(
            userProfileId = settingsStorage.getProfile()!!.id!!.toInt(),
            timePerformed = simpleDateFormat.format(Calendar.getInstance().time),
            success = true,
            points = points,
            score = score,
            credits = credits,
            challengeId = challengeItem.id,
            detailResult = detailResults,
            videoId = videoId
        )
        userService.storeResult(request)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                fetchDashboardData()
                refreshAttemptHistory()
//                fetchTimelineData()
            }, ::onError)
    }

    private fun fetchDashboardData() {
        userService.getDashboardData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                if (it != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        PraaktisDatabase.getInstance(getApplication()).getDashboardDao().apply {
                            val analysis = it.toAnalysisEntityList()
                            setDashboardData(
                                it.toDashboardEntity(),
                                analysis.first,
                                analysis.second,
                                analysis.third,
                                analysis.fourth
                            )
                        }
                    }
                    settingsStorage.setDashboard(it)
                }
            }, ::onError)
    }

    private fun fetchTimelineData() {
        userService.getTimelineData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                GlobalScope.launch(Dispatchers.IO) {
                    PraaktisDatabase.getInstance(getApplication()).getTimelineDao()
                        .removeAndInsertTimeline(it.toTimelineEntities())
                }
            }, ::onError)
    }

    private fun refreshAttemptHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                .removeAttemptHistory()
        }
    }

}