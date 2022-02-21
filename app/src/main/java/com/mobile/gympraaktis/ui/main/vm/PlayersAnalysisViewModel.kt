package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayersAnalysisViewModel(app: Application) : BaseViewModel(app) {

    init {

    }

    private val userServiceRepository: UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    fun observeFriends() =
        PraaktisDatabase.getInstance(getApplication()).getFriendsDao().getConfirmedFriends()
            .asLiveData()

     fun getFriends() {
        userServiceRepository.getFriends()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                viewModelScope.launch(Dispatchers.IO) {
                    PraaktisDatabase.getInstance(getApplication()).getFriendsDao()
                        .insertFriends(it.map { it.toEntity() })
                }
            }, ::onError)
    }


}