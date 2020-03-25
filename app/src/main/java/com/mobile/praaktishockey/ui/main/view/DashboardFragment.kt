package com.mobile.praaktishockey.ui.main.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.entities.DashboardDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.details.view.AnalysisFragment
import com.mobile.praaktishockey.ui.details.view.DetailsActivity
import com.mobile.praaktishockey.ui.main.adapter.AnalysisAdapter
import com.mobile.praaktishockey.ui.main.vm.DashboardViewModel
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment constructor(override val layoutId: Int = R.layout.fragment_dashboard) : BaseFragment() {

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
        (tv_score_current.layoutParams as LinearLayout.LayoutParams).weight = (currentScore + remainedScore) / 2f
        (tv_score_remained.layoutParams as LinearLayout.LayoutParams).weight = (currentScore + remainedScore) / 2f
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
            tv_score_current.text = "" + (tv_score_current.layoutParams as LinearLayout.LayoutParams).weight.toInt()
            tv_score_remained.text = "" + (tv_score_remained.layoutParams as LinearLayout.LayoutParams).weight.toInt()

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
