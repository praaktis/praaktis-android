package com.mobile.praaktishockey.ui.login.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.login.vm.LoginActivityViewModel

class LoginActivity constructor(override val layoutId: Int = R.layout.activity_login) :
    BaseActivity(), FragmentManager.OnBackStackChangedListener {

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

        fun startAndFinishAll(activity: Activity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

    }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()

        supportFragmentManager.addOnBackStackChangedListener(this)

        if (mViewModel?.isShowedInroPage() == true) {
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
    private var tempLogin: String? = null
    private var tempPassword: String? = null

    override fun onResume() {
        super.onResume()
        if (!isLoginProcessFinishSuccess) {
            tempToken?.let {
                loginStorage.token = it
            }
            tempLogin?.let {
                loginStorage.login = it
            }
            tempPassword?.let {
                loginStorage.password = it
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isLoginProcessFinishSuccess) {
            tempToken = loginStorage.token()
            tempLogin = loginStorage.login
            tempPassword = loginStorage.password
            loginStorage.logout()
        }
    }

    override fun onBackStackChanged() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        when (currentFragment) {
            is ConfirmLoginFragment -> {
                setLightNavigationBar()
            }
            else -> {
                clearLightNavigationBar()
            }
        }
    }

}

