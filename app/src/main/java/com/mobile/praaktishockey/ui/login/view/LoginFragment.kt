package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.login.vm.LoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_login) : BaseFragment() {

    companion object {
        val TAG = LoginFragment::class.java.simpleName
        fun getInstance(): Fragment = LoginFragment()
    }

    override val mViewModel: LoginFragmentViewModel
        get() = getViewModel { LoginFragmentViewModel(activity.application!!) }

    override fun initUI(savedInstanceState: Bundle?) {
        initClicks()
        initEvents()
    }

    private fun initClicks() {
        tilPassword.typeface = Typeface.createFromAsset(context?.assets, "fonts/abel_regular.ttf")

        cvCreateAccount.onClick {
            val tag = RegisterFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    RegisterFragment.getInstance(),
                    tag
                ).addToBackStack(tag)
            }
        }
        tvForgotPassword.onClick {
            val tag = ForgotPasswordFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    ForgotPasswordFragment.getInstance(),
                    tag
                )
                    .addToBackStack(tag)
            }
        }

        tvLogin.onClick {
            if (isValidLogin())
                mViewModel.login(etEmail.text.toString(), etPassword.text.toString())
        }
    }

    fun isValidLogin(): Boolean {
        var isValid = true
        if (etEmail.stringText().isEmpty() || !etEmail.isEmailValid()) {
            isValid = false
            tilEmail.error = getString(R.string.enter_valid_email)
        } else tilEmail.error = null

        if (etPassword.stringText().isEmpty() || etPassword.stringText().length < 8) {
            isValid = false
            tilPassword.error = getString(R.string.enter_valid_password)
        } else tilPassword.error = null
        return isValid
    }

    private fun initEvents() {
        mViewModel.loginEvent.observe(this, Observer {
            it?.let {
                val tag = showTagAccordingly(it)
                val fragment = when (tag) {
                    RegisterUserDetailFragment.TAG -> RegisterUserDetailFragment.getInstance()
                    AcceptTermsFragment.TAG -> AcceptTermsFragment.getInstance()
//                    CalibrateFragment.TAG -> CalibrateFragment.getInstance(true)
                    ConfirmLoginFragment.TAG -> ConfirmLoginFragment.getInstance()
                    else -> null
                }

                if (fragment != null) {
                    activity.showOrReplace(tag!!) {
                        add(
                            R.id.container,
                            fragment,
                            tag
                        ).addToBackStack(tag)
                    }
                } else { // all info is full
                    mViewModel.getLanguageObject()?.let { lang -> setLanguageAccordingly(lang) }
                    (activity as LoginActivity).isLoginProcessFinishSuccess = true
                    activity.finish()
                    MainActivity.start(activity)
                }
            }
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

    private fun showTagAccordingly(user: UserDTO): String? {
        if (user.firstName.isNullOrBlank()) {
            return RegisterUserDetailFragment.TAG
        } else {
            return if (!user.termsAccepted!!) {
                AcceptTermsFragment.TAG
            } else {
                if (user.scalingFactor == null) {
//                    CalibrateFragment.TAG
                    null
                } else {
                    if (!user.praaktisRegistered!!) {
                        ConfirmLoginFragment.TAG
                    } else {
                        null
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}
