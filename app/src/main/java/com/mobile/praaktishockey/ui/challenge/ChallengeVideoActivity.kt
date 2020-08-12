package com.mobile.praaktishockey.ui.challenge

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseActivity
import com.mobile.praaktishockey.databinding.ActivityVideoChallengeBinding
import com.mobile.praaktishockey.databinding.LayoutTargetBottomBinding
import com.mobile.praaktishockey.domain.common.AppGuide
import com.mobile.praaktishockey.domain.common.resettableLazy
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.activity_video_challenge.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChallengeVideoActivity(override val layoutId: Int = R.layout.activity_video_challenge) :
    BaseActivity<ActivityVideoChallengeBinding>(),
    FragmentManager.OnBackStackChangedListener {

    companion object {
        const val TAG: String = "ChallengeVideoActivity"

        @JvmStatic
        fun start(context: Context, challengeItem: ChallengeDTO) {
            val intent = Intent(context, ChallengeVideoActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            context.startActivity(intent)
        }
    }


    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as ChallengeDTO }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }
        hideSystemUI()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.addOnBackStackChangedListener(this)

        val video = when (challengeItem.id) {
            4 -> R.raw.handsup1
            5 -> R.raw.squats1
            6 -> R.raw.curl1
            7 -> R.raw.fw_lunge
            8 -> R.raw.bw_lunge
            else -> R.raw.challenge_video
        }

        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + video))
        videoView.setOnPreparedListener {
            ivPlayReply.show()
            videoView.start();
            videoView.pause();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = PlaybackParams()
//                playbackParams.speed = 0.5f
                it.playbackParams = playbackParams
//                ivPlayReply.hide()
            }
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
        }
        videoView.setOnInfoListener { mp, what, extra ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                //first frame was bufered - do your stuff here
                ivPlayReply.hide()
            }
            false
        }

        ivPlayReply.show()
        ivPlayReply.onClick {
            closeSpotlight()

            videoView.start()
            it.hide()
        }
        videoView.setOnCompletionListener {
            ivPlayReply.show()
        }

        tvCancel.onClick { finish() }
        tvNext.onClick {
            closeSpotlight()

            videoView.pause()
            ivPlayReply.show()

            val tag = ChallengeInstructionFragment.TAG
            replaceFragment(tag) {
                add(
                    R.id.container,
                    ChallengeInstructionFragment.getInstance(
                        challengeItem
                    ),
                    tag
                ).addToBackStack(tag)
            }
        }

        startGuideIfNecessary()

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            binding.videoView.pause()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackStackChanged() {
        if (supportFragmentManager.backStackEntryCount > 0)
            showSystemUI()
        else
            hideSystemUI()
    }

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.ivPlayReply.doOnPreDraw {
                spotlight.start()
            }
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(1000)
                binding.videoView.start()
            }
        }
        binding.ivInfo.setOnClickListener {
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

    private fun challengeVideoTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.closeTarget.hide()

        target.customText.text =
            "Play the video to see how the Challenge should be completed. Then click Next to get instructions on setting yourself up for the Challenge"

        target.root.updatePadding(bottom = binding.ivPlayReply.bottom)

        return Target.Builder()
            .setAnchor(binding.ivPlayReply)
            .setOverlay(target.root)
            .build()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(this)
            .setTargets(challengeVideoTarget())
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    binding.ivInfo.showAnimWithScale()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(1000)
                        binding.videoView.start()
                    }
                }
            })
            .build()
    }

}
