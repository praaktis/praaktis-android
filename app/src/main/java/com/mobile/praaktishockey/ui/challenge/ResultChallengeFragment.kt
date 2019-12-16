package com.mobile.praaktishockey.ui.challenge

import android.content.Intent
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.entities.DetailResult
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.vm.ResultChallengeFragmentViewModel
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import com.praaktis.exerciseengine.ExerciseEngineActivity
import kotlinx.android.synthetic.main.fragment_result_challenge.*

class ResultChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_result_challenge) :
    BaseFragment() {

    companion object {
        @JvmField
        val TAG = ResultChallengeFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(challengeItem: ChallengeItem): Fragment {
            val fragment = ResultChallengeFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ResultChallengeFragmentViewModel
        get() = getViewModel { ResultChallengeFragmentViewModel(activity.application) }

    private val challengeItem by lazy { arguments!!.getSerializable("challengeItem") as ChallengeItem }
    private val result by lazy { activity.intent.getFloatArrayExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) }

    override fun initUI(savedInstanceState: Bundle?) {
        initToolbar()
        initVideoView()
        initClicks()
    }

    private fun initToolbar() {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { activity.finish() }
    }

    private fun initVideoView() {
        videoView1.setVideoURI(Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.challenge_video))
        videoView1.setOnPreparedListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = PlaybackParams()
                playbackParams.speed = 0.5f
                it.playbackParams = playbackParams
            }
            ivPlay.show()
            it.pause()
        }
//        videoView2.setVideoURI(Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.vid_drag_flick))
//        videoView2.setOnPreparedListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val playbackParams = PlaybackParams()
//                playbackParams.speed = 0.5f
//                it.playbackParams = playbackParams
//            }
//            ivPlay.show()
//            it.pause()
//        }

        videoView1.setOnCompletionListener {
            ivPlay.show()
        }

        if (result != null) {
            val scoreOverAll = (result[0] * 0.45f + result[1] * 0.2f + result[2] * 0.35f)
            tvYourScore.text =
                "Your score: ${scoreOverAll.toInt()}"
            mViewModel.storeResult(
                challengeItem, (scoreOverAll/10).toInt(), scoreOverAll, 0f, mutableListOf(
                    DetailResult(20, result[0]),
                    DetailResult(21, result[1]),
                    DetailResult(22, result[2])
                )
            )
        }
    }

    private fun initClicks() {
        ivPlay.onClick {
            videoView1.start()
            videoView2.start()
            it.hide()
        }
        cvDetailAnalysis.onClick {
            videoView1.pause()
            videoView2.pause()
            ivPlay.show()

            val tag = DetailAnalysisFragment.TAG
            activity.showOrReplace(tag) {
                add(R.id.container, DetailAnalysisFragment.getInstance(challengeItem), tag)
                    .addToBackStack(tag)
            }
        }
        cvTryAgain.onClick {
            val intent = Intent(context, ExerciseEngineActivity::class.java)
            startActivityForResult(intent, 333)
        }
    }


}