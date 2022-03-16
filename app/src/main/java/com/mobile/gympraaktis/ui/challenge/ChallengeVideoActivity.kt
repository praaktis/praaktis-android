package com.mobile.gympraaktis.ui.challenge

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.databinding.ActivityVideoChallengeBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.StateBroadcastingVideoView
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
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
            4 -> R.raw.handsup2
            5 -> R.raw.squats2
            6 -> R.raw.curl2
            7 -> R.raw.fw_lunge2
            8 -> R.raw.bw_lunge2
            else -> R.raw.handsup2
        }

        binding.videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + video))
        binding.videoView.setOnPreparedListener {
            binding.ivPlayReply.show()
            binding.videoView.start()
            binding.videoView.pause()
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
        }
        /*binding.videoView.setOnInfoListener { mp, what, extra ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                //first frame was bufered - do your stuff here
                binding.ivPlayReply.invisible()
            }
            false
        }*/

        binding.videoView.setPlayPauseListener(object :
            StateBroadcastingVideoView.PlayPauseListener {
            override fun onPlay() {
                binding.ivPlayReply.invisible()
            }

            override fun onPause() {
                binding.ivPlayReply.show()
            }
        })

        binding.ivPlayReply.onClick {
            closeSpotlight()
            binding.videoView.start()
            it.invisible()
        }
        binding.videoView.setOnCompletionListener {
            ivPlayReply.show()
        }

        binding.tvCancel.onClick { finish() }
        binding.tvNext.onClick {
            closeSpotlight()

            binding.videoView.pause()
            binding.ivPlayReply.show()

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
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG + challengeItem.id)) {
            AppGuide.setGuideDone(TAG + challengeItem.id)
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
            binding.videoView.pause()
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

    private fun challengeVideoTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.closeTarget.hide()

        target.customText.text =
            "Play the video to see how the Challenge should be completed. Then click Next to get instructions on setting yourself up for the Challenge"

        target.customText2.text = challengeItem.videoGuide?.joinToString(separator = "\n") {
            it
        }

//        target.root.updatePadding(bottom = binding.ivPlayReply.bottom)
        target.customText.updatePadding(bottom = binding.ivPlayReply.bottom)
        target.customText2.updatePadding(top = binding.ivPlayReply.top - binding.ivPlayReply.height + 24.dp)

        return Target.Builder()
            .setAnchor(binding.ivPlayReply)
            .setOverlay(target.root)
            .build()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(this)
            .setTargets(challengeVideoTarget())
            .setBackgroundColor(R.color.primaryColor_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    isGuideStarted = false
                    binding.ivInfo.showAnimWithScale()
                    /*lifecycleScope.launch(Dispatchers.Main) {
                        delay(1000)
                        binding.videoView.start()
                    }*/
                }
            })
            .build()
    }

}
