package com.mobile.praaktishockey.ui.challenge

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.fragment.app.Fragment
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.entities.DetailResult
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.vm.ResultChallengeFragmentViewModel
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import com.praaktis.exerciseengine.Exercise
import com.praaktis.exerciseengine.ExerciseEngineActivity
import kotlinx.android.synthetic.main.fragment_result_challenge.*
import java.util.*

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
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }
    private val path by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.VIDEO_PATH) }

    private var mediaPlayer1: MediaPlayer? = MediaPlayer()
    private var mediaPlayer2: MediaPlayer? = MediaPlayer()
    private var timer: Timer? = null

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
//        videoView1.setVideoURI(Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.challenge_video))
//        videoView1.setOnPreparedListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val playbackParams = PlaybackParams()
//                playbackParams.speed = 0.5f
//                it.playbackParams = playbackParams
//            }
//            ivPlay.show()
//            it.pause()
//        }
        videoView1.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                val surface = Surface(surface)
                mediaPlayer1 = MediaPlayer()
                try {
                    mediaPlayer1?.setDataSource(
                        context, Uri.parse(
                            "android.resource://" +
                                    context?.packageName + "/" + R.raw.challenge_video
                        )
                    )
                    mediaPlayer1?.setSurface(surface)
                    mediaPlayer1?.setVolume(0f, 0f)
//                    mediaPlayer2?.isLooping = true
                    mediaPlayer1?.prepareAsync()
                    mediaPlayer1?.setOnPreparedListener { m ->
                        mediaPlayer1?.seekTo(100)
                        ivPlay.show()
                    }
                } catch (e: Exception) {

                }
            }
        }
        videoView2.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                val surface = Surface(surface)
                mediaPlayer2 = MediaPlayer()
                try {
                    mediaPlayer2?.setDataSource(path)
                    mediaPlayer2?.setSurface(surface)
                    mediaPlayer2?.setVolume(0f, 0f)
//                    mediaPlayer2?.isLooping = true
                    mediaPlayer2?.prepareAsync()
                    mediaPlayer2?.setOnPreparedListener { m ->
                        mediaPlayer2?.seekTo(6000)
                    }
                } catch (e: Exception) {

                }
            }
        }

//        mediaPlayer2?.setOnCompletionListener {
//            ivPlay.show()
//        }
//        videoView2.setVideoURI(Uri.parse(path))
//        videoView2.setOnPreparedListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val playbackParams = PlaybackParams()
//                playbackParams.speed = 0.5f
//                it.playbackParams = playbackParams
//            }
//            ivPlay.show()
//            it.pause()
//        }

//        videoView1.setOnCompletionListener {
//            ivPlay.show()
//        }

        if (result != null) {

            val detailResults = collectDetailResults()
            val scoreOverAll =
                getOverallScore()/*(result[0] * 0.45f + result[1] * 0.2f + result[2] * 0.35f)*/
            tvYourScore.text =
                "Your score: ${scoreOverAll.toInt()}"
            mViewModel.storeResult(
                challengeItem, (scoreOverAll / 10).toInt(), scoreOverAll, 0f, detailResults
                /*mutableListOf(
                    DetailResult(20, result[0]),
                    DetailResult(21, result[1]),
                    DetailResult(22, result[2])
                )*/
            )
        } else {
            tvYourScore.text = "Your score: 0"
        }

        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (ivPlay != null)
                    if (mediaPlayer2?.isPlaying == false) {
                        activity.runOnUiThread { ivPlay.show() }
                    } else {
                        activity.runOnUiThread { ivPlay.hide() }
                    }
            }
        }, 1000, 1000)
    }

    private fun getOverallScore(): Float {
        return result?.get("OVERALL") as Float? ?: 0f
    }

    private fun collectDetailResults(): MutableList<DetailResult> {
        val detailResults: MutableList<DetailResult> = mutableListOf()
        result?.forEach { (key, value) ->
            when (value) {
                is List<*> -> {
                    if (value.firstOrNull() is Float) {
                        @Suppress("UNCHECKED_CAST")
                        value as List<Float>
                        value.forEach { result ->
                            // todo:: send results to backend, @param(DetailResult.detailPointId) not receiving
                            detailResults.add(
                                DetailResult(
                                    11,
                                    result
                                )
                            )
                        }
                    }
                }
            }
        }
        return detailResults
    }

    private fun initClicks() {
        ivPlay.onClick {
            //            videoView1.start()
//            videoView2.start()
//            if (videoView1.currentPosition == 0)
            mediaPlayer1?.seekTo(0)
            mediaPlayer1?.start()

//            val oneSec = mediaPlayer2?.duration!! / 2
            mediaPlayer2?.seekTo(6000)
            mediaPlayer2?.start()
            it.hide()
        }
        cvDetailAnalysis.onClick {
            //            videoView1.pause()
//            videoView2.pause()
            if (result != null) {
                mediaPlayer1?.pause()
                mediaPlayer2?.pause()
                ivPlay.show()
                val tag = DetailAnalysisFragment.TAG
                activity.showOrReplace(tag) {
                    add(R.id.container, DetailAnalysisFragment.getInstance(challengeItem), tag)
                        .addToBackStack(tag)
                }
            } else {
                activity.makeToast("Failed exercise")
            }
        }
        cvTryAgain.onClick {
            val intent = Intent(context, ExerciseEngineActivity::class.java)
            intent.putExtra("LOGIN", mViewModel.getLogin())
            intent.putExtra("PASSWORD", mViewModel.getPassword())

            when(challengeItem.name){
                R.string.stretching_arms_up -> {
                    intent.putExtra("EXERCISE", Exercise.STRETCHING_ARMS_UP.ordinal)
                }

                R.string.squats -> {
                    intent.putExtra("EXERCISE", Exercise.SQUATS.ordinal)
                }

                R.string.curl -> {
                    intent.putExtra("EXERCISE", Exercise.CURL.ordinal)
                }
            }
            getActivity()!!.startActivityForResult(intent, 333)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer1 != null) {
            mediaPlayer1?.stop()
            mediaPlayer1?.release()
            mediaPlayer1 = null
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2?.stop()
            mediaPlayer2?.release()
            mediaPlayer2 = null
        }
        timer?.cancel()
        timer = null
    }

}