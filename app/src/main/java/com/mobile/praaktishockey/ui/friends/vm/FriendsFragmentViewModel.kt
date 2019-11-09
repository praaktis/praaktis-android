package com.mobile.praaktishockey.ui.friends.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.entities.FriendDTO

class FriendsFragmentViewModel (application: Application): BaseViewModel(application) {

    private val userServiceRepository: UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val friendsEvent: LiveEvent<List<FriendDTO>> = LiveEvent()
    val deleteFriendEvent: LiveEvent<Boolean> = LiveEvent()

    fun getFriends() {
        userServiceRepository.getFriends()
            .doOnSubscribe { showHideEvent.postValue(true)}
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                friendsEvent.postValue(it)
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