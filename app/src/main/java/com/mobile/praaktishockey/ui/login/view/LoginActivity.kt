package com.mobile.praaktishockey.ui.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.domain.extension.transparentStatusAndNavigationBar
import com.mobile.praaktishockey.ui.login.vm.LoginActivityViewModel

class LoginActivity constructor(override val layoutId: Int = R.layout.activity_login) : BaseActivity() {

    override val mViewModel: LoginActivityViewModel?
        get() = getViewModel { LoginActivityViewModel(application) }

    companion object {
        val REQUEST_LOGIN = 1

        fun start4Result(activity: BaseActivity) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivityForResult(
                intent,
                REQUEST_LOGIN
            )
        }

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (mViewModel?.isShowedInroPage()!!) {
            val tag = LoginFragment.TAG
            showOrReplace(tag) {
                replace(R.id.container, LoginFragment.getInstance(), tag)
            }
        } else {
            val tag = IntroPageFragment.TAG
            showOrReplace(tag) {
                replace(R.id.container, IntroPageFragment.getInstance(), tag)
            }
        }
    }

    private val loginStorage by lazy { SettingsStorage.instance }
    var isLoginProcessFinishSuccess = false
    private var tempToken: String? = null

    override fun onResume() {
        super.onResume()
        if (!isLoginProcessFinishSuccess) {
            tempToken?.let {
                loginStorage.token = it
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isLoginProcessFinishSuccess) {
            tempToken = loginStorage.token()
            loginStorage.logout()
        }
    }

}