package com.mobile.praaktishockey.ui.timeline.view

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentItemTimelineBinding
import com.mobile.praaktishockey.databinding.LayoutTargetTimelineBinding
import com.mobile.praaktishockey.domain.common.AppGuide
import com.mobile.praaktishockey.domain.common.Constants
import com.mobile.praaktishockey.domain.common.resettableLazy
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.entities.TimelineChallengeItem
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.timeline.adapter.TimelineAdapter
import com.mobile.praaktishockey.ui.timeline.vm.TimelineFragmentViewModel
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.fragment_item_timeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimelineItemFragment constructor(override val layoutId: Int = R.layout.fragment_item_timeline) :
    BaseFragment<FragmentItemTimelineBinding>() {

    companion object {
        val TAG = TimelineItemFragment::class.java.simpleName
        fun getInstance(timelineDTO: TimelineDTO): Fragment {
            val fragment = TimelineItemFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.TIMELINE, timelineDTO)
            fragment.arguments = bundle
            return fragment
        }

        fun getInstance(timelineChallengeItem: TimelineChallengeItem): Fragment {
            val fragment = TimelineItemFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.TIMELINE_CHALLENGE_ITEM, timelineChallengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: TimelineFragmentViewModel
        get() = getViewModel {
            TimelineFragmentViewModel(activity.application)
        }

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.fetchTimelineData()

        if (arguments != null) {
            if (arguments?.get(Constants.TIMELINE) != null) {
                val timeline = requireArguments().getSerializable(Constants.TIMELINE) as TimelineDTO
                val items = mutableListOf<ScoreDTO>()
                for (challenge in timeline.challenges) {
                    if (challenge.latest.timePerformed != null && challenge.latest.timePerformed != "") {
                        challenge.latest.name = challenge.name
                        items.add(challenge.latest)
                    }
                }
            } else if (arguments?.getSerializable(Constants.TIMELINE_CHALLENGE_ITEM) != null) {
                val challengeItem =
                    requireArguments().getSerializable(Constants.TIMELINE_CHALLENGE_ITEM) as TimelineChallengeItem
                challengeItem.scores.forEach {
                    it.name = challengeItem.name
                }
            }
        } else {
            val adapter = TimelineAdapter(
                onItemClick = {
                    closeSpotlight()

                    ChallengeActivity.start(activity, it)
                }
            )
            binding.rvTimeline.adapter = adapter

            mViewModel.observeTimeline().observe(viewLifecycleOwner, Observer {
                if (it.isNullOrEmpty()) tvNoData.show()
                else tvNoData.hide()
                if (it != null) adapter.submitList(it) {
                    startGuideIfNecessary(it.size)
                }
            })
        }
    }

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate

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
        spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(challengeTarget())
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    binding.ivInfo.showAnimWithScale()
                }
            })
            .build()
    }

    private fun challengeTarget(): Target {
        val target = LayoutTargetTimelineBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Shows you by date and time all of your attempts at different Challenges. Click on Details to get more information"

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