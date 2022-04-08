package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.*
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val authRepository by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }
    private val praaktisDao by lazy {
        PraaktisDatabase.getInstance(getApplication()).getPraaktisDao()
    }

    private val _challengesLiveData = MutableLiveData<List<ChallengeDTO>>()
    val challengesEvent: LiveData<List<ChallengeDTO>> get() = _challengesLiveData/*LiveEvent<List<ChallengeDTO>> = LiveEvent()*/
    private var challengesDisposable: Disposable? = null
    private var fcmDisposable: Disposable? = null

    fun getChallenges() {
        challengesDisposable = userRepository.getChallenges()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                this._challengesLiveData.postValue(it)
                settingsStorage.setChallenges(it)
            }, ::onError)
        addDisposable(challengesDisposable!!)
    }

    fun fetchDashboardData() {
        userRepository.getDashboardData()
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
                                analysis.fourth,
                                analysis.fifth,
                            )
                            insertRoutines(it.routines.map { it.toRoutineEntity() })
                        }
                    }
                }
            }, ::onError)
    }

    fun checkFcmToken() {
        if (!settingsStorage.isSentFcmToken) {
            fcmDisposable = authRepository.registerDevice(settingsStorage.fcmToken)
                .doOnSubscribe { showHideEvent.postValue(true) }
                .doAfterTerminate { showHideEvent.postValue(false) }
                .subscribe({
                    settingsStorage.isSentFcmToken = true
                }, ::onError)
        }
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        if (settingsStorage.getChallenges() != null)
            _challengesLiveData.postValue(settingsStorage.getChallenges())
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            praaktisDao.getOfflineExerciseResults().collectLatest {
                it.firstOrNull()?.let { result ->
                    userRepository.storeResult(result)
                        .doOnSubscribe { showHideEvent.postValue(true) }
                        .doAfterTerminate { showHideEvent.postValue(false) }
                        .subscribe({
                            viewModelScope.launch(Dispatchers.IO) {
                                praaktisDao.removeOfflineExerciseResult(result)
                            }
                            fetchDashboardData()
                            refreshAttemptHistory()
                        }, ::onError)
                }
            }
        }

    }

    private fun refreshAttemptHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                .removeAttemptHistory()
        }
    }


}

class UploadResultWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val storeResultModel: StoreResultModel
) : CoroutineWorker(appContext, workerParams) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val praaktisDao by lazy { PraaktisDatabase.getInstance(appContext).getPraaktisDao() }

    override suspend fun doWork(): Result {


        val result = kotlin.runCatching {
            userRepository.storeResultCoroutines(storeResultModel)
        }.onSuccess {

        }.onFailure {

        }


        return if (result.isSuccess) {
            val data = result.getOrNull()
            Result.success()
        } else {
            Result.failure()
        }

    }

}