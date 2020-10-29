package com.mobile.gympraaktis.ui.login.view

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentIntroPageBinding
import com.mobile.gympraaktis.domain.common.FadeTransformation
import com.mobile.gympraaktis.domain.extension.dp
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.ui.login.adapter.IntroInfiniteAdapter
import com.mobile.gympraaktis.ui.login.vm.IntroPageViewModel

class IntroPageFragment constructor(override val layoutId: Int = R.layout.fragment_intro_page) :
    BaseFragment<FragmentIntroPageBinding>() {

    companion object {
        const val TAG = "IntroPageFragment"
        fun getInstance() = IntroPageFragment()
    }

    override val mViewModel: IntroPageViewModel by viewModels()

    override fun onResume() {
        binding.vpLoop.resumeAutoScroll()
        super.onResume()
    }

    override fun onPause() {
        binding.vpLoop.pauseAutoScroll()
        super.onPause()
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.btnLogin.onClick {
            openLoginPage()
        }
        binding.btnRegister.onClick {
            openLoginPage()
            openRegisterPage()
        }

        binding.vpLoop.offscreenPageLimit = 5
        binding.vpLoop.setPageTransformer(true, FadeTransformation())
        binding.vpLoop.adapter = IntroInfiniteAdapter(
            requireContext(), listOf(
                Pair("Video Your\nChallenge", R.drawable.img_intro_1),
                Pair("Get Your\nScore", R.drawable.img_intro_4),
                Pair("Analyse Your\nPerformance", R.drawable.img_intro_3),
                Pair("Repeat and\nImprove", R.drawable.img_intro_2),
                Pair("Challenge\nFriends", R.drawable.img_intro_5),
            )
        )

        binding.vpIndicator.highlighterViewDelegate = {
            val highlighter = View(requireContext())
            highlighter.layoutParams = FrameLayout.LayoutParams(6.dp, 6.dp)
            highlighter.background = ShapeDrawable(OvalShape()).apply { paint.color = Color.WHITE }
            highlighter
        }
        binding.vpIndicator.unselectedViewDelegate = {
            val unselected = View(requireContext())
            unselected.layoutParams = LinearLayout.LayoutParams(6.dp, 6.dp)
            unselected.background = ShapeDrawable(OvalShape()).apply { paint.color = Color.WHITE }
            unselected.alpha = 0.4f
            unselected
        }

        binding.vpIndicator.updateIndicatorCounts(binding.vpLoop.indicatorCount)

        binding.vpLoop.onIndicatorProgress = { selectingPosition, progress ->
            binding.vpIndicator.onPageScrolled(selectingPosition, progress)
        }

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