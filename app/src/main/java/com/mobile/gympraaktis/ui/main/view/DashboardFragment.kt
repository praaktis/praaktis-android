package com.mobile.gympraaktis.ui.main.view

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.temp.BaseFragment
import com.mobile.gympraaktis.data.entities.DashboardWithAnalysis
import com.mobile.gympraaktis.databinding.FragmentDashboardBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.common.shape.CurvedEdgeTreatment
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.details.view.AnalysisFragment
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.main.adapter.AnalysisAdapter
import com.mobile.gympraaktis.ui.main.vm.DashboardViewModel
import com.mobile.gympraaktis.ui.main.vm.MainViewModel
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs

class DashboardFragment constructor(override val layoutId: Int = R.layout.fragment_dashboard) :
    BaseFragment<FragmentDashboardBinding>() {

    companion object {
        const val TAG: String = "DashboardFragment"
    }

    override val mViewModel: DashboardViewModel
        get() = getViewModel { DashboardViewModel(activity.application) }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var analysisAdapter: AnalysisAdapter

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.fetchDashboardData()

        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)

        setupCurvedLayout()

        initEvents()

        analysisAdapter = AnalysisAdapter {
            spotlight.finish()
            startActivity(
                DetailsActivity.start(activity, AnalysisFragment.TAG)
                    .putExtra(AnalysisFragment.TAG, it)
            )
        }
        binding.rvAnalysis.adapter = analysisAdapter

    }

    private fun initEvents() {
        mViewModel.observeDashboard().observe(viewLifecycleOwner, Observer {
            Timber.d("DASHBOARD_ENTITY $it")
            if (it != null) setDashboardData(it)
        })
    }

    private fun setDashboardData(dashboardData: DashboardWithAnalysis) {
        with(dashboardData) {
            analysisAdapter.submitList(dashboardData.analysis)
            binding.apply {
                tvLevel.text = "${dashboard.level}"
                tvPoints.text = "${dashboard.totalPoints}"
                tvCredits.text = "${dashboard.totalCredits}"
            }
            updateScoreProgress(
                dashboard.totalPoints,
                if (dashboard.pointsToNextLevel < 0) 0 else dashboard.pointsToNextLevel
            )
            startGuideIfNecessary()
        }
    }

    private fun updateScoreProgress(currentScore: Long, remainedScore: Long) {
        if (binding.vProgressCurrent.tag != currentScore) {
            binding.vProgressCurrent.tag = currentScore

            val maxScore = currentScore + remainedScore
            binding.tvScoreTotal.text = maxScore.toString()
            binding.llProgressLayout.weightSum = maxScore.toFloat()

            binding.vProgressCurrent.animateWeightChange(
                (binding.vProgressCurrent.layoutParams as LinearLayout.LayoutParams).weight.toInt(),
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

    private val spotlightDelegate = resettableLazy { initDashboardGuide() }
    private val spotlight by spotlightDelegate
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.constTop.doOnPreDraw {
                binding.progressLevel.doOnPreDraw {
                    binding.tvProgressTitle.doOnPreDraw {
                        binding.tvAnalysisTitle.doOnPreDraw {
                            spotlight.start()
                        }
                    }
                }
            }
        }
        binding.ivInfo.setOnClickListener {
            if (binding.nestedScroll.canScrollVertically(-1)) {
                binding.nestedScroll.fullScroll(View.FOCUS_UP)
                binding.nestedScroll.smoothScrollTo(0, 0)
            }
            restartSpotlight()
        }
    }

    private fun restartSpotlight() {
        if (spotlightDelegate.isInitialized())
            spotlightDelegate.reset()
        spotlight.start()
    }

    fun nextTarget() {
        spotlight.next()
    }

    fun closeSpotlight() {
        if (isGuideStarted)
            spotlight.finish()
    }

    private fun initDashboardGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets((activity as MainActivity).bottomNavTarget(), firstTarget(), secondTarget())
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    isGuideStarted = false
                    binding.ivInfo.showAnimWithScale()
                }
            })
            .build()
    }

    private fun firstTarget(): Target {
        val firstTarget = LayoutTargetBinding.inflate(layoutInflater)

        firstTarget.closeTarget.setOnClickListener { nextTarget() }
        firstTarget.closeSpotlight.setOnClickListener { closeSpotlight() }
        firstTarget.customText.text =
            "Shows your current Level, Points and Credits. You earn Points for achieving a score better than 80% on any Challenge up to a maximum of 15 and you earn 1 Credit for each 5 attempts at Challenges"

        val progressLocation = IntArray(2)
        binding.progressLevel.getLocationInWindow(progressLocation)

        val constTopLocation = IntArray(2)
        binding.constTop.getLocationInWindow(constTopLocation)

        val shapeHeight = progressLocation[1].toFloat()
        val shapeWidth = binding.constTop.width.toFloat() - 10.dp

        firstTarget.root.updatePadding(top = binding.progressLevel.height + progressLocation[1])

        return Target.Builder()
            .setAnchor(
                progressLocation[0] + binding.constTop.width / 2f,
                (progressLocation[1] + constTopLocation[1]) / 2f
            )
            .setOverlay(firstTarget.root)
            .setShape(RoundedRectangle(shapeHeight, shapeWidth, 4.dp.toFloat()))
            .build()
    }

    private fun secondTarget(): Target {
        val secondTarget = LayoutTargetBottomBinding.inflate(layoutInflater)

        val rvAnalysisLocation = IntArray(2)
        binding.rvAnalysis.getLocationOnScreen(rvAnalysisLocation)

        val rvAnalysisVisibleRect = Rect()
        binding.rvAnalysis.getLocalVisibleRect(rvAnalysisVisibleRect)

        val tvAnalysisLocation = IntArray(2)
        binding.tvAnalysisTitle.getLocationInWindow(tvAnalysisLocation)

        secondTarget.closeTarget.setOnClickListener { nextTarget() }
        secondTarget.closeSpotlight.setOnClickListener { closeSpotlight() }
        secondTarget.customText.text =
            "Shows your scores and attempts for each Challenge and comparison with Friends and other Users at your level, age and experience"

        secondTarget.root.updatePadding(bottom = tvAnalysisLocation[1] - binding.tvAnalysisTitle.height)

        return Target.Builder()
            .setAnchor(
                rvAnalysisLocation[0] + binding.rvAnalysis.width / 2f,
                tvAnalysisLocation[1] + binding.tvAnalysisTitle.height / 2f + rvAnalysisVisibleRect.height() / 2f
            )
            .setOverlay(secondTarget.root)
            .setShape(
                RoundedRectangle(
                    (rvAnalysisVisibleRect.height() + binding.tvAnalysisTitle.height).toFloat() + 20.dp,
                    binding.rvAnalysis.width.toFloat() + 20.dp,
                    4.dp.toFloat()
                )
            )
            .build()
    }

}
