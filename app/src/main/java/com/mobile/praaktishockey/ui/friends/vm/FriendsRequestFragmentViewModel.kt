package com.mobile.praaktishockey.ui.friends.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.entities.FriendDTO

class FriendsRequestFragmentViewModel (application: Application): BaseViewModel(application) {

    private val userServiceRepository: UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val refuseFriendEvent: LiveEvent<Boolean> = LiveEvent()
    val confirmFriendEvent: LiveEvent<Boolean> = LiveEvent()
    val friendRequestEvent: LiveEvent<List<FriendDTO>> = LiveEvent()
    val inviteFriendEvent: LiveEvent<String> = LiveEvent()

    fun getFriendRequests() {
        userServiceRepository.getFriendRequest()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                friendRequestEvent.postValue(it)
            }, ::onError)
    }

    fun refuseFriend(email: String) {
        userServiceRepository.refuseFriend(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                refuseFriendEvent.postValue(true)
            }, ::onError)
    }

    fun confirmFriend(email: String) {
        userServiceRepository.confirmFriend(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                confirmFriendEvent.postValue(true)
            }, ::onError)
    }

    fun inviteFriend(email: String) {
        userServiceRepository.inviteFriend(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({ inviteFriendEvent.postValue(it.message) }, ::onError)
    }

}