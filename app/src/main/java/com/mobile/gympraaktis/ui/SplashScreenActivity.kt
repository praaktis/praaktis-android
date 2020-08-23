package com.mobile.gympraaktis.ui

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.temp.BaseActivity
import com.mobile.gympraaktis.databinding.LayoutStartPageBinding
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.LanguageItem
import com.mobile.gympraaktis.domain.entities.UserDTO
import com.mobile.gympraaktis.domain.extension.getViewModel
import com.mobile.gympraaktis.domain.extension.hide
import com.mobile.gympraaktis.domain.extension.show
import com.mobile.gympraaktis.domain.extension.transparentStatusAndNavigationBar
import com.mobile.gympraaktis.ui.login.view.LoginActivity
import com.mobile.gympraaktis.ui.login.vm.LoginFragmentViewModel
import com.mobile.gympraaktis.ui.main.view.MainActivity

class SplashScreenActivity constructor(override val layoutId: Int = R.layout.layout_start_page) :
    BaseActivity<LayoutStartPageBinding>() {

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
        })
        mViewModel?.connectionErrorEvent?.observe(this, Observer {
            if (mViewModel!!.getProfile() != null)
                MainActivity.startAndFinishAll(this)
            else LoginActivity.startAndFinishAll(this)
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

    private fun getSplashScreenDuration() = 1500L

    private fun routeToAppropriatePage(user: UserDTO?) {
        // Example routing
        when {
            user == null -> LoginActivity.startAndFinishAll(this)
            else -> {
                mViewModel!!.getProfile()?.let {
//                    if (it.language != null)
                    MainActivity.startAndFinishAll(this)
//                    else LoginActivity.start(this)
                }
            }
        }
    }

    override fun showProgress() {
        binding.progressCircular.show()
    }

    override fun hideProgress() {
        binding.progressCircular.hide()
    }

}