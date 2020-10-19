package com.mobile.gympraaktis.ui.login.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentAcceptTermsBinding
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.LanguageItem
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.ui.login.vm.RegisterUserDetailViewModel
import com.mobile.gympraaktis.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_accept_terms.*

class AcceptTermsFragment constructor(override val layoutId: Int = R.layout.fragment_accept_terms) :
    BaseFragment<FragmentAcceptTermsBinding>() {

    companion object {
        const val TAG = "AcceptTermsFragment"
        fun getInstance(): Fragment = AcceptTermsFragment()
    }

    override val mViewModel: RegisterUserDetailViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        tvFinish.onClick {
            if (chbTermsConditions.isChecked) {
                mViewModel.acceptTerms()
            } else {
                activity.makeToast(getString(R.string.read_terms_conditions))
            }
        }

        tvAcceptTerms.onClick {
            mViewModel.getTermsConditions()
        }

        mViewModel.acceptTermsEvent.observe(this, Observer {
//            val tag = CalibrateFragment.TAG
//            activity.showOrReplace(tag) {
//                add(
//                    R.id.container,
//                    CalibrateFragment.getInstance(true),
//                    tag
//                ).addToBackStack(tag)
//            }
            mViewModel.loadProfile()
        })
        mViewModel.getAcceptTermsEvent.observe(this, Observer {
            val url = it
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(browserIntent)
        })

        mViewModel.profileInfoEvent.observe(this, Observer {
            if (it.praaktisRegistered!!) {
                if (it.language != null)
                    setLanguageAccordingly(mViewModel.getLanguageObject()!!)
                (activity as LoginActivity).isLoginProcessFinishSuccess = true
                MainActivity.startAndFinishAll(activity)
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

        tvBack.onClick { activity.supportFragmentManager.popBackStack() }
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