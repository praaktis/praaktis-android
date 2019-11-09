package com.mobile.praaktishockey.ui.login.view

import android.os.Bundle
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.login.vm.ConfirmLoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_calibrate.*

class CalibrateFragment constructor(override val layoutId: Int = R.layout.fragment_calibrate) : BaseFragment() {

    companion object {
        val TAG = CalibrateFragment::class.java.simpleName
        fun getInstance(isFromLogin: Boolean) = CalibrateFragment().apply {
            arguments = Bundle().apply {
                putBoolean("isFromLogin", isFromLogin)
            }
        }
    }

    override val mViewModel: ConfirmLoginFragmentViewModel
        get() = getViewModel { ConfirmLoginFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        val isFromLogin = arguments!!.getBoolean("isFromLogin")

//        InstructionDialog(context!!).show()

        mViewModel.profileInfoEvent.observe(this, Observer {
            if (it.praaktisRegistered!!) {
                if (it.language != null)
                    setLanguageAccordingly(mViewModel.getLanguageObject()!!)
                (activity as LoginActivity).isLoginProcessFinishSuccess = true
                activity.finish()
                MainActivity.start(activity)
            } else {
                val tag = ConfirmLoginFragment.TAG
                activity.showOrReplace(tag) {
                    add(
                        R.id.container,
                        ConfirmLoginFragment.getInstance(),
                        tag
                    )
                        .addToBackStack(tag)
                }
            }
        })

        tvOk.onClick {
            if (isFromLogin) {
                mViewModel.loadProfile()
            } else {
                activity.onBackPressed()
            }
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