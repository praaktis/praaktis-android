package com.mobile.praaktishockey.ui

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.transparentStatusAndNavigationBar
import com.mobile.praaktishockey.ui.login.view.LoginActivity
import com.mobile.praaktishockey.ui.login.vm.LoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity

class SplashScreenActivity constructor(override val layoutId: Int = R.layout.layout_start_page) :
    BaseActivity() {

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        scheduleSplashScreen()
    }

    override val mViewModel: LoginFragmentViewModel?
        get() = getViewModel { LoginFragmentViewModel(application) }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed({ mViewModel?.loadProfile() }, splashScreenDuration)

        mViewModel?.loginEvent?.observe(this, Observer {
            mViewModel!!.getLanguageObject()?.let { lang -> setLanguageAccordingly(lang) }
            routeToAppropriatePage(it)
            finish()
        })
        mViewModel?.connectionErrorEvent?.observe(this, Observer {
            if (mViewModel!!.getProfile() != null)
                MainActivity.start(this)
            else LoginActivity.start(this)
            finish()
        })
    }

    private fun setLanguageAccordingly(language: LanguageItem) {
        val localeKey = when (language.key) {
            1 -> "en"
            2 -> "fr"
            else -> "en"
        }
        SettingsStorage.instance.lang = localeKey
    }

    private fun getSplashScreenDuration() = 2000L

    private fun routeToAppropriatePage(user: UserDTO?) {
        // Example routing
        when {
            user == null -> LoginActivity.start(this)
            else -> {
                mViewModel!!.getProfile()?.let {
//                    if (it.language != null)
                    MainActivity.start(this)
//                    else LoginActivity.start(this)
                }
            }
        }
    }
}