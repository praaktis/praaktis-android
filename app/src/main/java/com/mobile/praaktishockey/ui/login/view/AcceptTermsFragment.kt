package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.login.vm.RegisterUserDetailViewModel
import kotlinx.android.synthetic.main.fragment_accept_terms.*
import android.content.Intent
import android.net.Uri

class AcceptTermsFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_accept_terms) : BaseFragment() {

    companion object {
        val TAG = AcceptTermsFragment::class.java.simpleName
        fun getInstance(): Fragment = AcceptTermsFragment()
    }

    override val mViewModel: RegisterUserDetailViewModel
        get() = getViewModel { RegisterUserDetailViewModel(activity.application!!) }

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
            val tag = CalibrateFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    CalibrateFragment.getInstance(true),
                    tag
                ).addToBackStack(tag)
            }
        })
        mViewModel.getAcceptTermsEvent.observe(this, Observer {
            val url = it
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(browserIntent)
        })

        tvBack.onClick { fragmentManager?.popBackStack() }
    }
}