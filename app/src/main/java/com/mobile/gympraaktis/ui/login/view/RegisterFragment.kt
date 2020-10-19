package com.mobile.gympraaktis.ui.login.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentRegisterBinding
import com.mobile.gympraaktis.domain.extension.hideKeyboard
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.ui.login.vm.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment constructor(override val layoutId: Int = R.layout.fragment_register) :
    BaseFragment<FragmentRegisterBinding>() {

    companion object {
        const val TAG: String = "RegisterFragment"
        fun getInstance(): androidx.fragment.app.Fragment = RegisterFragment()
    }

    override val mViewModel: RegisterViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        initClicks()
        initEvents()
    }

    private fun initClicks() {

        binding.btnBack.onClick {
            activity.currentFocus?.let {
                activity.hideKeyboard(it)
            }
            activity.supportFragmentManager.popBackStack()
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

            submitWith(binding.btnSubmit) {
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

}