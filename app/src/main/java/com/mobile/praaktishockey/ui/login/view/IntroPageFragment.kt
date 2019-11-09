package com.mobile.praaktishockey.ui.login.view

import android.annotation.SuppressLint
import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.FadeTransformation
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.login.adapter.IntroPagerAdapter
import com.mobile.praaktishockey.ui.login.vm.IntroPageViewModel
import kotlinx.android.synthetic.main.fragment_intro_page.*

class IntroPageFragment @SuppressLint("ValidFragment")
constructor(override val layoutId: Int = R.layout.fragment_intro_page)
    : BaseFragment() {

    companion object {
        val TAG = IntroPageFragment::class.java.simpleName
        fun getInstance() : androidx.fragment.app.Fragment = IntroPageFragment()
    }

    override val mViewModel: IntroPageViewModel
        get() = getViewModel{ IntroPageViewModel(activity.application!!)}

    override fun initUI(savedInstanceState: Bundle?) {
        /*tvSkip.onClick { */layoutStartPage.hide() /*}*/
        cvLogin.onClick {
            val tag = LoginFragment.TAG
            activity.showOrReplace(tag) {
                add(R.id.container, LoginFragment.getInstance(), tag)
            }
            mViewModel.setShowedInroPage(true)
        }
        vpIntro.adapter = IntroPagerAdapter()
        vpIntro.setPageTransformer(false, FadeTransformation())

//        val radius = 0f
//
//        val decorView = activity.window.getDecorView()
//        val rootView = decorView.findViewById(android.R.id.content) as ViewGroup
//        val windowBackground = decorView.getBackground()
//
//        bv.setupWith(rootView)
//                .setFrameClearDrawable(windowBackground)
//                .setBlurAlgorithm(RenderScriptBlur(context))
//                .setBlurRadius(radius)
//                .setHasFixedTransformationMatrix(true)
    }

    override fun onStart() {
        super.onStart()
//        bvStartPage.startBlur()
    }

    override fun onStop() {
        super.onStop()
//        bvStartPage.pauseBlur()
    }
}