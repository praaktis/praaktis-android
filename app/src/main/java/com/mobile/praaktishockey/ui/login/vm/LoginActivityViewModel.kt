package com.mobile.praaktishockey.ui.login.vm

import android.app.Application
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage

class LoginActivityViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }

    fun isShowedInroPage(): Boolean = loginStorage.isShowedIntroPage()

}