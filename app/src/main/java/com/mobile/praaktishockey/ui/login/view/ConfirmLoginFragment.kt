package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.login.vm.ConfirmLoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_confirm_login.*

class ConfirmLoginFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_confirm_login) : BaseFragment() {

    companion object {
        val TAG = ConfirmLoginFragment::class.java.simpleName

        fun getInstance() = ConfirmLoginFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override val mViewModel: ConfirmLoginFragmentViewModel
        get() = getViewModel { ConfirmLoginFragmentViewModel(activity.application!!) }

    private var user: UserDTO? = null

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.loadProfile()

        mViewModel.profileInfoEvent.observe(this, Observer {
            user = it
            setInfo(user!!)
        })

        tvStart.onClick {
            mViewModel.loadProfile()
        }
    }

    private fun setInfo(user: UserDTO) {
        tvThankYou.text = String.format(getString(R.string.thank_you_s), user.firstName)
        tvActivationSummary.text = String.format(getString(R.string.account_activation_summary), user.email)
        if (user.praaktisRegistered!!) {
            if (user.language != null)
                setLanguageAccordingly(mViewModel.getLanguageObject()!!)
            (activity as LoginActivity).isLoginProcessFinishSuccess = true
            activity.finish()
            MainActivity.start(activity)
        } else {
            activity.makeToast(getString(R.string.please_activate_link))
        }
    }

    private fun setLanguageAccordingly(language: LanguageItem) {
        val localeKey = when (language.key) {
            1 -> "en"
            2 -> "fr"
            else -> "en"
        }
        SettingsStorage.instance.lang = localeKey
    }
}
