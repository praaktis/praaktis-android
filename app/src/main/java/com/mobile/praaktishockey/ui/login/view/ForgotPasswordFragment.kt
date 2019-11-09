package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hideKeyboard
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.login.vm.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_forgot_password) : BaseFragment() {

    companion object {
        val TAG: String = ForgotPasswordFragment::class.java.simpleName
        fun getInstance() = ForgotPasswordFragment()
    }

    override val mViewModel: ForgotPasswordViewModel
        get() = getViewModel { ForgotPasswordViewModel(activity.application!!) }

    override fun initUI(savedInstanceState: Bundle?) {

        cvBackToLogin.onClick {
            activity.currentFocus?.let {
                activity.hideKeyboard(it)
            }
            fragmentManager?.popBackStack()
        }

        mViewModel.forgotPasswordEvent.observe(this, Observer {
            activity.makeToast("Success")
            fragmentManager?.popBackStack()
        })

        form {
            useRealTimeValidation()
            inputLayout(tilEmail) {
                isEmail().description(getString(R.string.invalid_email))
            }
            submitWith(tvSubmit) {
                mViewModel.forgotPassword(etEmail.text.toString())
            }
        }
    }
}