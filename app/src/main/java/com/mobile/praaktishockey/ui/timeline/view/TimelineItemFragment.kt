package com.mobile.praaktishockey.ui.timeline.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.Constants
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.entities.TimelineChallengeItem
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.timeline.adapter.TimelineAdapter
import com.mobile.praaktishockey.ui.timeline.vm.TimelineFragmentViewModel
import kotlinx.android.synthetic.main.fragment_item_timeline.*

class TimelineItemFragment constructor(override val layoutId: Int = R.layout.fragment_item_timeline) :
    BaseFragment() {

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
                    ChallengeActivity.start(activity, it)
                },
                isEmptySet = {
                    if (it) tvNoData.show()
                    else tvNoData.hide()
                })
            rv_timeline.adapter = adapter

            mViewModel.getTimelineData()
            mViewModel.timelineDataEvent.observe(this, Observer {
                adapter.submitList(it.challenges)
            })
        }
    }

}