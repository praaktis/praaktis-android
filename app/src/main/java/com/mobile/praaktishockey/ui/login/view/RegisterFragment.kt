package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hideKeyboard
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.login.vm.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_register) : BaseFragment() {

    companion object {
        val TAG: String = RegisterFragment::class.java.simpleName
        fun getInstance(): androidx.fragment.app.Fragment = RegisterFragment()
    }

    override val mViewModel: RegisterViewModel
        get() = getViewModel { RegisterViewModel(activity.application!!) }

    override fun initUI(savedInstanceState: Bundle?) {
        initClicks()
        initEvents()
    }

    private fun initClicks() {
        tilPassword.typeface = Typeface.createFromAsset(context?.assets, "fonts/abel_regular.ttf")
        tilRePassword.typeface = Typeface.createFromAsset(context?.assets, "fonts/abel_regular.ttf")

        cvBackToLogin.onClick {
            activity.currentFocus?.let {
                activity.hideKeyboard(it)
            }
            fragmentManager?.popBackStack()
        }

        form {
            useRealTimeValidation()
            inputLayout(tilEmail) {
                isEmail().description(getString(R.string.invalid_email))
            }
            inputLayout(tilPassword) {
                length().atLeast(8).description(getString(R.string.at_least_8_characters))
                conditional({ !etRePassword.text.isNullOrBlank() }) {
                    assert(getString(R.string.password_donot_match)) {
                        etRePassword.text.toString() == etPassword.text.toString()
                    }
                }
            }
            inputLayout(tilRePassword) {
                length().atLeast(8).description(getString(R.string.at_least_8_characters))
                assert(getString(R.string.password_donot_match)) {
                    etRePassword.text.toString() == etPassword.text.toString()
                }
            }
            submitWith(tvSendConfirmationLink) {
                mViewModel.createUser(etEmail.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun initEvents() {
        mViewModel.createUserEvent.observe(this, Observer {})
        mViewModel.loginEvent.observe(this, Observer {
            val tag = RegisterUserDetailFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    RegisterUserDetailFragment.getInstance(),
                    tag
                ).addToBackStack(tag)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onStop() {
        super.onStop()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}