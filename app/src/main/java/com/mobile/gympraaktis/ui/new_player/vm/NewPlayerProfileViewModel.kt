package com.mobile.gympraaktis.ui.new_player.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.GenderDTO
import com.mobile.gympraaktis.domain.entities.KeyValueDTO
import com.mobile.gympraaktis.domain.entities.PlayerCreateModel
import org.json.JSONObject

class NewPlayerProfileViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    init {
        fetchHeight()
        fetchWeight()
        fetchAgeOptions()
        fetchGender()
    }

    val createPlayerEvent: LiveEvent<String> = LiveEvent()

    fun createPlayer(player: PlayerCreateModel) {
        userRepository.createPlayer(player)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                val json = JSONObject(it.string())
                val message = json.optString("message")
                createPlayerEvent.postValue(message)
            }, ::onError)
    }

    private val _heightLiveData = MutableLiveData<List<KeyValueDTO>>()
    val heightLiveData get() = _heightLiveData

    private fun fetchHeight() {
        userRepository.fetchHeightOptions()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                _heightLiveData.postValue(it)
            }, ::onError)
    }

    private val _weightLiveData = MutableLiveData<List<KeyValueDTO>>()
    val weightLiveData get() = _weightLiveData

    private fun fetchWeight() {
        userRepository.fetchWeightOptions()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                _weightLiveData.postValue(it)
            }, ::onError)
    }

    private val _ageLiveData = MutableLiveData<List<KeyValueDTO>>()
    val ageLiveData get() = _ageLiveData

    private fun fetchAgeOptions() {
        userRepository.fetchAgeOptions()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                _ageLiveData.postValue(it)
            }, ::onError)
    }

    private val _genderLiveData = MutableLiveData<List<GenderDTO>>()
    val genderLiveData get() = _genderLiveData

    private fun fetchGender() {
        userRepository.fetchGenderOptions()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                _genderLiveData.postValue(it)
            }, ::onError)
    }


}
