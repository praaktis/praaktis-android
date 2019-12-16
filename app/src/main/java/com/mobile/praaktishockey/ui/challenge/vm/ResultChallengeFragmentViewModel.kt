package com.mobile.praaktishockey.ui.challenge.vm

import android.app.Application
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.entities.DetailResult
import com.mobile.praaktishockey.domain.entities.StoreResultDTO
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import java.text.SimpleDateFormat
import java.util.*

class ResultChallengeFragmentViewModel(application: Application) : BaseViewModel(application) {

    private val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    fun storeResult(challengeItem: ChallengeItem,
                    points: Int,
                    score: Float,
                    credits: Float,
                    detailResults: List<DetailResult>) {
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
            .doAfterTerminate { showHideEvent.postValue(false)}
            .subscribe({

            }, ::onError)
    }

}