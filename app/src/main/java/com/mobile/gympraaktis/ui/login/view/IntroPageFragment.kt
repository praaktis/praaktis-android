package com.mobile.gympraaktis.ui.login.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentIntroPageBinding
import com.mobile.gympraaktis.domain.common.FadeTransformation
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

    override val mViewModel: IntroPageViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.btnLogin.onClick {
            openLoginPage()
        }
        binding.btnRegister.onClick {
            openLoginPage()
            openRegisterPage()
        }

        binding.vpIntro.adapter = IntroPagerAdapter()
        binding.vpIntro.setPageTransformer(false, FadeTransformation())
    }

    private fun openLoginPage() {
        mViewModel.setShowedInroPage(true)
        val tag = LoginFragment.TAG
        activity.showOrReplace(tag) {
            add(R.id.container, LoginFragment.getInstance(), tag)
        }
    }

    private fun openRegisterPage() {
        val tag = RegisterFragment.TAG
        activity.showOrReplace(tag) {
            add(R.id.container, RegisterFragment.getInstance(), tag).addToBackStack(tag)
        }
    }

}