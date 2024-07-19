package com.mobile.gympraaktis.ui.login.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentLoginBinding
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.LanguageItem
import com.mobile.gympraaktis.domain.entities.UserDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.login.vm.LoginFragmentViewModel
import com.mobile.gympraaktis.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment constructor(override val layoutId: Int = R.layout.fragment_login) :
    BaseFragment<FragmentLoginBinding>() {

    companion object {
        const val TAG = "LoginFragment"
        fun getInstance(): Fragment = LoginFragment()
    }

    override val mViewModel: LoginFragmentViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        initClicks()
        initEvents()
    }

    private fun initClicks() {
        binding.btnCreateAccount.onClick {
            val tag = RegisterFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    RegisterFragment.getInstance(),
                    tag
                ).addToBackStack(tag)
            }
        }
        binding.btnForgotPassword.onClick {
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

        binding.btnLogin.onClick {
            if (isValidLogin())
                mViewModel.login(etEmail.text.toString(), etPassword.text.toString())
        }
    }

    private fun isValidLogin(): Boolean {
        var isValid = true
        if (etEmail.stringText().isEmpty() || !etEmail.isEmailValid()) {
            isValid = false
            etEmail.error = getString(R.string.enter_valid_email)
        } else etEmail.error = null

        if (etPassword.stringText().isEmpty() || etPassword.stringText().length < 8) {
            isValid = false
            etPassword.error = getString(R.string.enter_valid_password)
        } else etPassword.error = null
        return isValid
    }

    private fun initEvents() {
        mViewModel.loginEvent.observe(this, Observer {
            it?.let {
                val tag = showTagAccordingly(it)
                val fragment = when (tag) {
                    RegisterUserDetailFragment.TAG -> RegisterUserDetailFragment.getInstance()
                    AcceptTermsFragment.TAG -> AcceptTermsFragment.getInstance()
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
                    MainActivity.startAndFinishAll(activity)
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
        return if (user.firstName.isNullOrBlank()) {
            RegisterUserDetailFragment.TAG
        } else {
            if (!user.termsAccepted!!) {
                AcceptTermsFragment.TAG
            } else {
                if (user.praaktisRegistered == true) {
                    null
                } else {
                    ConfirmLoginFragment.TAG
                }
            }
        }
    }

}