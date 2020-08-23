package com.mobile.gympraaktis.ui.friends.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.FriendDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsFragmentViewModel (application: Application): BaseViewModel(application) {

    private val userServiceRepository: UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val friendsEvent: LiveEvent<List<FriendDTO>> = LiveEvent()
    val deleteFriendEvent: LiveEvent<Boolean> = LiveEvent()

    fun getFriends() {
        userServiceRepository.getFriends()
            .doOnSubscribe { showHideEvent.postValue(true)}
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                viewModelScope.launch {
                    val confirmedFriends = it.filter {
                        it.friendStatus == "Confirmed"
                    }
                    withContext(Dispatchers.Main) {
                        friendsEvent.postValue(confirmedFriends)
                    }
                }
            }, ::onError)
    }

    fun deleteFriend(email: String) {
        userServiceRepository.deleteFriend(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false)}
            .subscribe({
                deleteFriendEvent.postValue(true)
            }, ::onError)
    }


}