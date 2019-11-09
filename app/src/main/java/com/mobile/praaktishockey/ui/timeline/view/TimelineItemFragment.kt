package com.mobile.praaktishockey.ui.timeline.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.Constants
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.entities.TimelineChallengeItem
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.timeline.vm.TimelineFragmentViewModel
import kotlinx.android.synthetic.main.fragment_item_timeline.*

class TimelineItemFragment constructor(override val layoutId: Int = R.layout.fragment_item_timeline) : BaseFragment() {

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
        get() = getViewModel { ViewModelProviders.of(activity).get(TimelineFragmentViewModel::class.java) }

    private val scores = mutableListOf<ScoreDTO>()

    override fun initUI(savedInstanceState: Bundle?) {
        initClicks()
        if (arguments!!.get(Constants.TIMELINE) != null) {
            val timeline = arguments!!.getSerializable(Constants.TIMELINE) as TimelineDTO
            val items = mutableListOf<ScoreDTO>()
            for (challenge in timeline.challenges) {
                if (challenge.latest.timePerformed != null && challenge.latest.timePerformed != "") {
                    challenge.latest.name = challenge.name
                    items.add(challenge.latest)
                }
            }
            setScoreData(items)
        } else {
            val challengeItem = arguments!!.getSerializable(Constants.TIMELINE_CHALLENGE_ITEM) as TimelineChallengeItem
            challengeItem.scores.forEach {
                it.name = challengeItem.name
            }
            setScoreData(challengeItem.scores)
        }
    }

    private fun initClicks() {
        tvDetail1.onClick {
            ChallengeActivity.start(activity, scores[0])
        }
        tvDetail2.onClick {
            ChallengeActivity.start(activity, scores[1])
        }
        tvDetail3.onClick {
            ChallengeActivity.start(activity, scores[2])
        }
    }

    private fun setScoreData(scores: List<ScoreDTO>) {
        this.scores.clear()
        this.scores.addAll(scores)
        for (i in 0 until scores.size) {
            val tvTimePerformed = when (i) {
                0 -> tvTimePerformed1
                1 -> tvTimePerformed2
                else -> tvTimePerformed3
            }
            val tvChallengeName = when (i) {
                0 -> tvChallengeName1
                1 -> tvChallengeName2
                else -> tvChallengeName3
            }
            val tvPoints = when (i) {
                0 -> tvPoints1
                1 -> tvPoints2
                else -> tvPoints3
            }
            val tvScore = when (i) {
                0 -> tvScore1
                1 -> tvScore2
                else -> tvScore3
            }

            when (i) {
                0 -> cv1.show()
                1 -> cv2.show()
                else -> cv3.show()
            }

            tvTimePerformed.text = scores[i].timePerformed
            tvChallengeName.text = scores[i].name
            tvPoints.text = "" + scores[i].points
            tvScore.text = "" + scores[i].score
        }
        if(scores.isEmpty()) tvNoData.show()
        else tvNoData.hide()
    }
}