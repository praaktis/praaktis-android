package com.mobile.gympraaktis.ui.challenge.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.data.entities.RoutineEntity
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.DetailResult
import com.mobile.gympraaktis.domain.entities.StoreResultModel
import com.praaktis.exerciseengine.Engine.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ResultChallengeFragmentViewModel(application: Application) : BaseViewModel(application) {

    private val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val praaktisDao by lazy {
        PraaktisDatabase.getInstance(getApplication()).getPraaktisDao()
    }

    fun storeResult(
        challengeItem: RoutineEntity,
        points: Int? = null,
        score: Float,
        credits: Float? = null,
        detailResults: List<DetailResult>,
        videoId: String? = null,
        player: PlayerEntity,
        measurements: List<Measurement>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val request = StoreResultModel(
                playerName = player.name,
                userProfileId = player.id,
                timePerformed = simpleDateFormat.format(Calendar.getInstance().time),
                success = true,
                points = points,
                score = score,
                credits = credits,
                challengeId = challengeItem.id.toInt(),
                detailResult = detailResults,
                videoId = videoId,
                measurements = measurements
            )
            praaktisDao.saveResult(request)
        }
//        userRepository.storeResult(request)
//            .doOnSubscribe { showHideEvent.postValue(true) }
//            .doAfterTerminate { showHideEvent.postValue(false) }
//            .subscribe({
//                praaktisDao.removeOfflineExerciseResult(request)
//                fetchDashboardData()
//                refreshAttemptHistory()
////                fetchTimelineData()
//            }, ::onError)
    }

}