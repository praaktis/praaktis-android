package com.mobile.gympraaktis.ui.login.view

import android.os.Bundle
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.temp.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentIntroPageBinding
import com.mobile.gympraaktis.domain.common.FadeTransformation
import com.mobile.gympraaktis.domain.extension.getViewModel
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.ui.login.adapter.IntroPagerAdapter
import com.mobile.gympraaktis.ui.login.vm.IntroPageViewModel

class IntroPageFragment constructor(override val layoutId: Int = R.layout.fragment_intro_page) :
    BaseFragment<FragmentIntroPageBinding>() {

    companion object {
        const val TAG = "IntroPageFragment"
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