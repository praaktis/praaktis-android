package com.mobile.gympraaktis.ui.players

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.entities.toPlayerEntity
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.PlayerDTO
import com.mobile.gympraaktis.domain.entities.PlayerUpdateModel
import org.json.JSONObject

class MyPlayersViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    private val dao = PraaktisDatabase.getInstance(getApplication()).getDashboardDao()

    val playerResultEvent: LiveEvent<PlayerDTO> = LiveEvent()

    fun getPlayerProfile(playerId: Long) {
        userRepository.getPlayerProfile(playerId)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                playerResultEvent.postValue(it)
                dao.updatePlayer(it.toPlayerEntity())
            }, ::onError)
    }

    val updatePlayerEvent: LiveEvent<String> = LiveEvent()

    fun updatePlayer(player: PlayerUpdateModel) {
        userRepository.updatePlayer(player)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                getPlayerProfile(player.playerId)
                val json = JSONObject(it.string())
                val message = json.optString("message")
                updatePlayerEvent.postValue(message)
            }, ::onError)
    }

}