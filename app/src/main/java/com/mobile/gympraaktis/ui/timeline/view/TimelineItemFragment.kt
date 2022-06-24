package com.mobile.gympraaktis.ui.timeline.view

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.Result
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.databinding.FragmentItemTimelineBinding
import com.mobile.gympraaktis.databinding.LayoutTargetTimelineBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.challenge.ChallengeActivity
import com.mobile.gympraaktis.ui.timeline.adapter.TimelinePagedAdapter
import com.mobile.gympraaktis.ui.timeline.vm.TimelineFragmentViewModel
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimelineItemFragment constructor(override val layoutId: Int = R.layout.fragment_item_timeline) :
    BaseFragment<FragmentItemTimelineBinding>() {

    companion object {
        const val TAG = "TimelineItemFragment"
        fun getInstance() = TimelineItemFragment()
    }

    override val mViewModel: TimelineFragmentViewModel by viewModels()

    private var onRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null

    override fun initUI(savedInstanceState: Bundle?) {
        if (activity.isConnected()) {
            mViewModel.refreshAttemptHistory()
        }
        mViewModel.pagingStateLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Result.Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                Result.Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvNoData.hide()
                    startGuideIfNecessary(1)
                }
                Result.Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Result.Status.EMPTY -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvNoData.show()
                    startGuideIfNecessary(0)
                }
            }
        }

        val adapter = TimelinePagedAdapter(
            onItemClick = {
                closeSpotlight()
                ChallengeActivity.start(activity, it)
            }
        )
        binding.rvTimeline.adapter = adapter

        var pagedListLiveData =
            mViewModel.getPagedAttemptHistory((binding.dropdownSelectPlayer.tag as? PlayerEntity)?.id)

        pagedListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            mViewModel.refreshAttemptHistory()
//            if (adapter.currentList.isNullOrEmpty()) {
                pagedListLiveData.removeObservers(viewLifecycleOwner)
                pagedListLiveData =
                    mViewModel.getPagedAttemptHistory((binding.dropdownSelectPlayer.tag as? PlayerEntity)?.id)
                pagedListLiveData.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
//            }
        }

        binding.swipeRefresh.setOnRefreshListener(onRefreshListener)

        setupSelectPlayerField()
    }

    private fun setupSelectPlayerField() {
        mViewModel.observePlayers().observe(viewLifecycleOwner) {
            val allPlayers = PlayerEntity(-1L, getString(R.string.all_players))
            val players = it.toMutableList().apply { add(0, allPlayers) }

            binding.dropdownSelectPlayer.setDropdownValues(players.map { it.name }.toTypedArray())
            binding.dropdownSelectPlayer.setOnItemClickListener { parent, view, position, id ->
                players.getOrNull(position)?.let {
                    binding.dropdownSelectPlayer.tag = it
                }
                binding.swipeRefresh.post {
                    binding.swipeRefresh.isRefreshing = true
                    onRefreshListener?.onRefresh()
                }
            }
        }
    }

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate
    private var isGuideStarted = false

    private fun startGuideIfNecessary(listSize: Int) {
        if (!AppGuide.isGuideDone(TAG)) {
            if (listSize > 0) {
                AppGuide.setGuideDone(TAG)
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.rvTimeline.doOnPreDraw {
                        spotlight.start()
                    }
                }
            }
        }
        binding.ivInfo.setOnClickListener {
            binding.rvTimeline.smoothScrollToPosition(0)
            restartSpotlight()
        }
    }

    private fun restartSpotlight() {
        if (spotlightDelegate.isInitialized())
            spotlightDelegate.reset()
        spotlight.start()
    }

    private fun closeSpotlight() {
        if (isGuideStarted)
            spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(challengeTarget())
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

    private fun challengeTarget(): Target {
        val target = LayoutTargetTimelineBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Shows you by date and time all of your attempts at different Routines. Click on Details to get more information"

        val rvLocation = IntArray(2)
        binding.rvTimeline.getLocationOnScreen(rvLocation)

        val rvVisibleRect = Rect()
        binding.rvTimeline.getLocalVisibleRect(rvVisibleRect)

        val itemHeight = binding.rvTimeline.getChildAt(0)?.height ?: 300.dp

        target.customText.updatePadding(top = rvLocation[1] + itemHeight + 22.dp)

        return Target.Builder()
            .setAnchor(
                rvLocation[0] + binding.rvTimeline.width / 2f,
                rvLocation[1] + itemHeight / 2f + 22.dp
            )
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    itemHeight.toFloat() + 20.dp,
                    binding.rvTimeline.width.toFloat() - 16.dp,
                    4.dp.toFloat()
                )
            )
            .build()
    }

}