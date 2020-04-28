package com.mobile.praaktishockey.ui.login.view

import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentIntroPageBinding
import com.mobile.praaktishockey.domain.common.FadeTransformation
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.login.adapter.IntroPagerAdapter
import com.mobile.praaktishockey.ui.login.vm.IntroPageViewModel

class IntroPageFragment constructor(override val layoutId: Int = R.layout.fragment_intro_page) :
    BaseFragment<FragmentIntroPageBinding>() {

    companion object {
        val TAG = IntroPageFragment::class.java.simpleName
        fun getInstance() = IntroPageFragment()
    }

    override val mViewModel: IntroPageViewModel
        get() = getViewModel { IntroPageViewModel(activity.application!!) }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.btnLogin.onClick {
            mViewModel.setShowedInroPage(true)
            val tag = LoginFragment.TAG
            activity.showOrReplace(tag) {
                add(R.id.container, LoginFragment.getInstance(), tag)
            }
        }
        binding.vpIntro.adapter = IntroPagerAdapter()
        binding.vpIntro.setPageTransformer(false, FadeTransformation())
    }

}