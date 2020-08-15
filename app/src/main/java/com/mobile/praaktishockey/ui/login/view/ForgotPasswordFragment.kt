package com.mobile.praaktishockey.ui.login.view

import android.os.Bundle
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentForgotPasswordBinding
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hideKeyboard
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.login.vm.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment constructor(override val layoutId: Int = R.layout.fragment_forgot_password) :
    BaseFragment<FragmentForgotPasswordBinding>() {

    companion object {
        const val TAG: String = "ForgotPasswordFragment"
        fun getInstance() = ForgotPasswordFragment()
    }

    override val mViewModel: ForgotPasswordViewModel
        get() = getViewModel { ForgotPasswordViewModel(activity.application!!) }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.btnBack.onClick {
            activity.currentFocus?.let {
                activity.hideKeyboard(it)
            }
            activity.supportFragmentManager.popBackStack()
        }

        mViewModel.forgotPasswordEvent.observe(this, Observer {
            activity.makeToast("Success")
            activity.supportFragmentManager.popBackStack()
        })

        form {
            inputLayout(tilEmail) {
                isEmail().description(getString(R.string.invalid_email))
            }
            submitWith(binding.btnSubmit) {
                mViewModel.forgotPassword(etEmail.text.toString())
            }
        }
    }
}