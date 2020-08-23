package com.mobile.gympraaktis.ui.login.vm

import android.app.Application
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage

class AcceptTermsFragmentViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }

}