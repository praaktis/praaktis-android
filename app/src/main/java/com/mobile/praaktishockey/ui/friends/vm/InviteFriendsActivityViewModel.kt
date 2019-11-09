package com.mobile.praaktishockey.ui.friends.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.LiveEvent

class InviteFriendsActivityViewModel(application: Application) : BaseViewModel(application) {

    private val userServiceRepository: UserServiceRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val inviteFriendEvent: LiveEvent<String> = LiveEvent()

    fun inviteFriend(email: String) {
        userServiceRepository.inviteFriend(email)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({ inviteFriendEvent.postValue(it.message) }, ::onError)
    }

}