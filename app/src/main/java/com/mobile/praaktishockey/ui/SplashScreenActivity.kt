package com.mobile.praaktishockey.ui

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.provider.FirebaseInitProvider
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.login.view.LoginActivity
import com.mobile.praaktishockey.ui.login.vm.LoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.OnSuccessListener
import android.util.Log


class SplashScreenActivity
constructor(override val layoutId: Int = R.layout.layout_start_page) : BaseActivity() {

    override fun initUI(savedInstanceState: Bundle?) {
        scheduleSplashScreen()
    }

    override val mViewModel: LoginFragmentViewModel?
        get() = getViewModel { LoginFragmentViewModel(application) }

    private fun scheduleSplashScreen() {
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this,
//            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
//                val newToken = instanceIdResult.token
//                Log.e("newToken", newToken)

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
//            })
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
                    if (it.language != null)
                        MainActivity.start(this)
                    else LoginActivity.start(this)
                }
            }
        }
    }
}