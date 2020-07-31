package com.mobile.praaktishockey.ui.main.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.AuthSeriviceRepository
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import io.reactivex.disposables.Disposable

class MainViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val authRepository by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    private val _challengesLiveData = MutableLiveData<List<ChallengeDTO>>()
    val challengesEvent: LiveData<List<ChallengeDTO>> get() = _challengesLiveData/*LiveEvent<List<ChallengeDTO>> = LiveEvent()*/
    private var challengesDisposable: Disposable? = null
    private var fcmDisposable: Disposable? = null

    init {
        getServerName()
    }

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
//        challengesEvent.postValue()
        if (settingsStorage.getChallenges() != null)
            _challengesLiveData.postValue(settingsStorage.getChallenges())
    }

    fun getServerName() {
        commonsRepo.getServerName().subscribe({
            settingsStorage.praaktisServerName = it.get(it.keys.first()) ?: ""
        }, {})
    }
}