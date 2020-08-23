package com.mobile.gympraaktis.ui.friends.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent

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