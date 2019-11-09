package com.mobile.praaktishockey.ui.challenge

import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.vm.ResultChallengeFragmentViewModel
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import kotlinx.android.synthetic.main.fragment_result_challenge.*

class ResultChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_result_challenge)
    : BaseFragment() {

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

    private val challengeItem by lazy { arguments!!.getSerializable("challengeItem") as ChallengeItem}

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
        videoView1.setVideoURI(Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.vid_drag_flick))
        videoView1.setOnPreparedListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = PlaybackParams()
                playbackParams.speed = 0.5f
                it.playbackParams = playbackParams
            }
            ivPlay.show()
            it.pause()
        }
        videoView2.setVideoURI(Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.vid_drag_flick))
        videoView2.setOnPreparedListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = PlaybackParams()
                playbackParams.speed = 0.5f
                it.playbackParams = playbackParams
            }
            ivPlay.show()
            it.pause()
        }

        videoView1.setOnCompletionListener {
            ivPlay.show()
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
    }
}