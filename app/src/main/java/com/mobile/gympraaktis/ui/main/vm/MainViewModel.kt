package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import io.reactivex.disposables.Disposable

class MainViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val authRepository by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

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

}