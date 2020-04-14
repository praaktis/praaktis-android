package com.mobile.praaktishockey.ui.main.view

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentDashboardBinding
import com.mobile.praaktishockey.domain.common.shape.CurvedEdgeTreatment
import com.mobile.praaktishockey.domain.entities.DashboardDTO
import com.mobile.praaktishockey.domain.extension.animateWeightChange
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
import timber.log.Timber
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
        lifecycleScope.launch(Dispatchers.Default) {
            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val curveSize = size.x * 0.22f

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
                        abs(scrollY).toFloat() / (binding.clContent.getChildAt(0).top - 16.dp)/*v.maxScrollAmount*/ // calculate offset percentage
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
        val maxScore = currentScore + remainedScore
        binding.tvScoreTotal.text = maxScore.toString()
        binding.llProgressLayout.weightSum = maxScore.toFloat()
        binding.vProgressCurrent.animateWeightChange(
            0,
            currentScore.toInt(),
            duration = 1500,
            startDelay = 200,
            init = { interpolator = FastOutSlowInInterpolator() },
            onValueChange = {
                binding.tvScoreCurrent.apply {
                    text = it.toInt().toString()
                    translationX = binding.vProgressCurrent.width.toFloat()
                }
            })
    }

}
