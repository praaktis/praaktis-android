package com.mobile.praaktishockey.ui.main.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.firebase.iid.FirebaseInstanceId
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.db.PraaktisDatabase
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    val loginStorage by lazy { SettingsStorage.instance }

    fun logout() {
        userService.logout()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                    loginStorage.fcmToken = it.token
                    loginStorage.isSentFcmToken = false
                }
                viewModelScope.launch(Dispatchers.IO) {
                    PraaktisDatabase.getInstance(getApplication()).clearAllTables()
                    loginStorage.logout()
                    logoutEvent.postValue(true)
                }
            }, ::onError)
    }

}