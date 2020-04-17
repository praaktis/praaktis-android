package com.mobile.praaktishockey.ui.challenge.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.db.PraaktisDatabase
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.entities.*
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
        detailResults: List<DetailResult>
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
            detailResult = detailResults
        )
        userService.storeResult(request)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                fetchDashboardData()
                fetchTimelineData()
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


}