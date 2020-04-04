package com.mobile.praaktishockey.ui.main.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentDashboardBinding
import com.mobile.praaktishockey.domain.common.shape.CurvedEdgeTreatment
import com.mobile.praaktishockey.domain.entities.DashboardDTO
import com.mobile.praaktishockey.domain.extension.dp
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.updatePadding
import com.mobile.praaktishockey.ui.details.view.AnalysisFragment
import com.mobile.praaktishockey.ui.details.view.DetailsActivity
import com.mobile.praaktishockey.ui.main.adapter.AnalysisAdapter
import com.mobile.praaktishockey.ui.main.vm.DashboardViewModel
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs


class DashboardFragment constructor(override val layoutId: Int = R.layout.fragment_dashboard) :
    BaseFragment<FragmentDashboardBinding>() {

    companion object {
        val TAG: String = DashboardFragment::class.java.simpleName
    }

    override val mViewModel: DashboardViewModel
        get() = getViewModel { DashboardViewModel(activity.application) }

    private lateinit var mainViewModel: MainViewModel
    private var dashboardData: DashboardDTO? = null
    private lateinit var analysisAdapter: AnalysisAdapter

    override fun initUI(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
        mainViewModel.changeTitle(getString(R.string.dashboard))

        setupCurvedLayout()

        mViewModel.getDashboardData()
        initEvents()

        analysisAdapter = AnalysisAdapter {
            if (dashboardData != null)
                startActivity(
                    DetailsActivity.start(activity, AnalysisFragment.TAG)
                        .putExtra(AnalysisFragment.TAG, it)
                        .putExtra(AnalysisFragment.CHALLENGES, dashboardData)
                )
        }
        rv_analysis.adapter = analysisAdapter
    }

    private fun setupCurvedLayout() {
        binding.root.post {
            lifecycleScope.launch(Dispatchers.Default) {
                val curveSize = binding.root.width * 0.22f

                withContext(Dispatchers.Main) {
                    binding.cvCurvedLayout.apply {
                        clipToOutline = false
                        binding.clContent.updatePadding(top = curveSize.toInt())
//                        setContentPadding(0, curveSize.toInt(), 0, 0)
                    }
                }

                suspend fun setCurvedLayout(curvedPercent: Float) {
                    binding.cvCurvedLayout.apply {
                        val shapeAppearanceModel = ShapeAppearanceModel.Builder()
                            .setTopEdge(CurvedEdgeTreatment(curveSize * curvedPercent))
                            .build()
                        withContext(Dispatchers.Main) {
                            binding.cvCurvedLayout.shapeAppearanceModel = shapeAppearanceModel
                        }
                    }
                }

                setCurvedLayout(1f)

                binding.nestedScroll.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                    lifecycleScope.launch(Dispatchers.Default) {
                        val percentage =
                            abs(scrollY).toFloat() / (binding.clContent.getChildAt(0).top -16.dp)/*v.maxScrollAmount*/ // calculate offset percentage
                        Log.d(TAG, "SCROLLY: $scrollY")
                        Log.d(TAG, "MAXSCROLLAMOUNT: ${v.maxScrollAmount}")
                        Log.d(TAG, percentage.toString())

                        if (percentage <= 1) {
                            setCurvedLayout(1 - percentage)
                            withContext(Dispatchers.Main) {
                                binding.constTop.translationY = -(16.dp * percentage)
                            }
                        } else {
                            setCurvedLayout(0f)
                            withContext(Dispatchers.Main) {
                                binding.constTop.translationY = -16.dp.toFloat()
                            }
                        }

                    }

                }

/*
                binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    lifecycleScope.launch(Dispatchers.Default) {
                        val percentage = abs(verticalOffset)
                            .toFloat() / appBarLayout.totalScrollRange // calculate offset percentage
                        setCurvedLayout(percentage)
                    }
                })
*/
            }
        }

    }

    private fun initEvents() {
        mViewModel.dashboardEvent.observe(this, Observer {
            setDashboardData(it)
        })
    }

    private fun setDashboardData(dashboardData: DashboardDTO) {
        this.dashboardData = dashboardData
        with(dashboardData) {
            analysisAdapter.submitList(dashboardData.challenges.toList())
            tv_level.text = "$level"
            tv_points.text = "$totalPoints"
            tv_credits.text = "$totalCredits"
            updateScoreProgress(totalPoints, if (pointsToNextLevel < 0) 0 else pointsToNextLevel)
        }
    }

    private fun updateScoreProgress(currentScore: Long, remainedScore: Long) {
        val delta = (currentScore - remainedScore) / 2
        (tv_score_current.layoutParams as LinearLayout.LayoutParams).weight =
            (currentScore + remainedScore) / 2f
        (tv_score_remained.layoutParams as LinearLayout.LayoutParams).weight =
            (currentScore + remainedScore) / 2f
        tv_score_remained.requestLayout()
        tv_score_current.requestLayout()

        val valueAnimator = ValueAnimator.ofFloat(0f, delta.toFloat())
        valueAnimator.duration = 500
        valueAnimator.startDelay = 200
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            if (tv_score_current == null) return@addUpdateListener
            val v = it.animatedValue as Float
            (tv_score_current.layoutParams as LinearLayout.LayoutParams).weight =
                (currentScore + remainedScore) / 2f + v
            (tv_score_remained.layoutParams as LinearLayout.LayoutParams).weight =
                (currentScore + remainedScore) / 2f - v
            tv_score_current.text =
                "" + (tv_score_current.layoutParams as LinearLayout.LayoutParams).weight.toInt()
            tv_score_remained.text =
                "" + (tv_score_remained.layoutParams as LinearLayout.LayoutParams).weight.toInt()

            tv_score_remained.requestLayout()
            tv_score_current.requestLayout()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (tv_score_current == null) return
                tv_score_current.text = "$currentScore"
                tv_score_remained.text = "$remainedScore"
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        valueAnimator.start()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
            mViewModel.getDashboardData()
    }
}
