package com.mobile.gympraaktis.ui.main.view

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.DashboardEntity
import com.mobile.gympraaktis.databinding.FragmentDashboardBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.common.shape.CurvedEdgeTreatment
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.main.adapter.AnalysisPagerAdapter
import com.mobile.gympraaktis.ui.main.vm.DashboardViewModel
import com.mobile.gympraaktis.ui.main.vm.MainViewModel
import com.mobile.gympraaktis.ui.subscription_plans.view.SubscriptionPlansFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max

class DashboardFragment constructor(override val layoutId: Int = R.layout.fragment_dashboard) :
    BaseFragment<FragmentDashboardBinding>() {

    companion object {
        const val TAG: String = "DashboardFragment"
    }

    override val mViewModel: DashboardViewModel by viewModels()

    private lateinit var mainViewModel: MainViewModel

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.showHideLoader.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }
        binding.swipeRefresh.setOnRefreshListener {
            mViewModel.fetchDashboardData()
        }

        mViewModel.fetchDashboardData()

        mainViewModel = ViewModelProvider(activity)[MainViewModel::class.java]

        setupCurvedLayout()

        initEvents()

//        exerciseAnalysisAdapter = ExerciseAnalysisAdapter {
//            spotlight.finish()
//            startActivity(
//                DetailsActivity.start(activity, AnalysisFragment.TAG)
//                    .putExtra(AnalysisFragment.TAG, it)
//            )
//        }
        binding.vpAnalysis.adapter = AnalysisPagerAdapter(childFragmentManager, lifecycle)
        binding.vpAnalysis.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.btgAnalysis.check(binding.btgAnalysis.get(position).id)
            }
        })

        binding.btgAnalysis.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                binding.vpAnalysis.currentItem = group.indexOfChild(group.findViewById(checkedId))
            }
        }

    }

    private fun initEvents() {
        mViewModel.observeDashboard().observe(viewLifecycleOwner) {
            if (it != null) setDashboardData(it)
        }

        mViewModel.observePlayerAnalysis().observe(viewLifecycleOwner) {
            Timber.d(it.toString())
        }
    }

    var subscriptionAlertDialog: AlertDialog? = null

    private fun setDashboardData(dashboard: DashboardEntity) {
            binding.apply {
                tvLevel.text = "${dashboard.level}"
                tvPlayers.text = "${dashboard.activePlayers}"
                tvAttempts.text = "${dashboard.recordedAttempts}"
            }
            updateScoreProgress(
                dashboard.activePlayers,
                dashboard.allowedPlayers
            )
            updateAttemptsProgress(
                dashboard.recordedAttempts,
                dashboard.recordedAttempts + dashboard.attemptsAvailable
            )
            startGuideIfNecessary()

            warnUserDependingDashboardData(dashboard)
    }

    private fun warnUserDependingDashboardData(dashboard: DashboardEntity) {
        if (subscriptionAlertDialog?.isShowing == true) return

        if ((dashboard.activePlayers + 1 >= dashboard.allowedPlayers || dashboard.recordedAttempts + 1 >= dashboard.recordedAttempts + dashboard.attemptsAvailable)) {
            val word2 = when {
                dashboard.activePlayers + 1 >= dashboard.allowedPlayers && dashboard.recordedAttempts + 1 >= dashboard.recordedAttempts + dashboard.attemptsAvailable -> "players and attempts"
                dashboard.activePlayers + 1 >= dashboard.allowedPlayers -> "players"
                dashboard.recordedAttempts + 1 >= dashboard.recordedAttempts + dashboard.attemptsAvailable -> "attempts"
                else -> "players and attempts"
            }

            val word1 = when {
                dashboard.activePlayers >= dashboard.allowedPlayers || dashboard.recordedAttempts >= dashboard.recordedAttempts + dashboard.attemptsAvailable -> ""
                else -> "almost"
            }

            subscriptionAlertDialog = activity.materialAlert {
                setMessage(
                    "You are $word1 out\n" +
                            "of $word2.\n" +
                            "\n" +
                            "Please click here \n" +
                            "to subscribe for more"
                )
                setPositiveButton("Subscribe") { dialog, which ->
                    startActivity(DetailsActivity.start(activity, SubscriptionPlansFragment.TAG))
                    dialog.dismiss()
                }
                setNegativeButton("Later") { dialog, which ->

                }
            }.apply { show() }
        }
    }

    private fun updateScoreProgress(currentScore: Long, maxScore: Long) {
        if (binding.vProgressCurrent.tag != currentScore) {
            binding.vProgressCurrent.tag = currentScore

            binding.tvScoreTotal.text = maxScore.toString()
            binding.llProgressLayout.weightSum = max(currentScore, maxScore).toFloat()

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

    private fun updateAttemptsProgress(currentScore: Long, maxScore: Long) {
        if (binding.vAttemptProgressCurrent.tag != currentScore) {
            binding.vAttemptProgressCurrent.tag = currentScore

            binding.tvAttemptScoreTotal.text = maxScore.toString()
            binding.llProgressAttemptLayout.weightSum = max(currentScore, maxScore).toFloat()

            binding.vAttemptProgressCurrent.animateWeightChange(
                (binding.vAttemptProgressCurrent.layoutParams as LinearLayout.LayoutParams).weight.toInt(),
                currentScore.toInt(),
                duration = 1500,
                startDelay = 200,
                init = { interpolator = FastOutSlowInInterpolator() },
                onValueChange = {
                    binding.tvAttemptScoreCurrent.apply {
                        text = it.toInt().toString()
                        translationX = binding.vAttemptProgressCurrent.width.toFloat()
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
            .setBackgroundColor(R.color.primaryColor_alpha_90)
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
            "Shows your current Subscription, the number of Players you are allowed to Activate and the Number of Attempts you can video. The bars help you manage your subscription by showing how many Players you have Activated and Attempts you have used. A warning message will be displayed when you are close to your limit."

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
        binding.vpAnalysis.getLocationOnScreen(rvAnalysisLocation)

        val rvAnalysisVisibleRect = Rect()
        binding.vpAnalysis.getLocalVisibleRect(rvAnalysisVisibleRect)

        val tvAnalysisLocation = IntArray(2)
        binding.tvAnalysisTitle.getLocationInWindow(tvAnalysisLocation)

        secondTarget.closeTarget.setOnClickListener { nextTarget() }
        secondTarget.closeSpotlight.setOnClickListener { closeSpotlight() }
        secondTarget.customText.text =
            "Shows your scores and attempts for each Routine and comparison with other Players at your level, age and experience"

        secondTarget.root.updatePadding(bottom = tvAnalysisLocation[1] - binding.tvAnalysisTitle.height)

        return Target.Builder()
            .setAnchor(
                rvAnalysisLocation[0] + binding.vpAnalysis.width / 2f,
                tvAnalysisLocation[1] + binding.tvAnalysisTitle.height / 2f + rvAnalysisVisibleRect.height() / 2f
            )
            .setOverlay(secondTarget.root)
            .setShape(
                RoundedRectangle(
                    (rvAnalysisVisibleRect.height() + binding.tvAnalysisTitle.height).toFloat() + 20.dp,
                    binding.vpAnalysis.width.toFloat() + 20.dp,
                    4.dp.toFloat()
                )
            )
            .build()
    }

}
