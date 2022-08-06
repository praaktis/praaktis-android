package com.mobile.gympraaktis.ui.challenge

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.data.entities.RoutineEntity
import com.mobile.gympraaktis.databinding.ActivityVideoChallengeBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


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
        fun start(context: Context, challengeItem: RoutineEntity, player: PlayerEntity) {
            val intent = Intent(context, ChallengeVideoActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            intent.putExtra("player", player)
            context.startActivity(intent)
        }
    }

    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as RoutineEntity }
    private val player by lazy { intent.getSerializableExtra("player") as PlayerEntity }

    private val mHttpDataSourceFactory: HttpDataSource.Factory by lazy {
        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
    }

    //    private val mDefaultDataSourceFactory: DefaultDataSource.Factory by lazy {
//        DefaultDataSource.Factory(applicationContext, mHttpDataSourceFactory)
//    }
    private val mCacheDataSourceFactory: DataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
    private var exoPlayer: ExoPlayer? = null
    private val cache: SimpleCache = PraaktisApp.cache

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(DefaultMediaSourceFactory(mCacheDataSourceFactory))
            .build()
            .also { exoPlayer ->
                binding.exoPlayer.player = exoPlayer

                val videoUri = Uri.parse(challengeItem.videoUrl)
                val mediaItem = MediaItem.fromUri(videoUri)
                val mediaSource =
                    ProgressiveMediaSource.Factory(mCacheDataSourceFactory)
                        .createMediaSource(mediaItem)
                exoPlayer.volume = 0f
                exoPlayer.setMediaSource(mediaSource, true)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()

                exoPlayer.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            binding.ivPlayReply.hide()
                        } else {
                            binding.ivPlayReply.show()
                        }
                    }
                })
            }
    }

    override fun onStart() {
        super.onStart()


        val fragments = supportFragmentManager.fragments
        Timber.d("FRAGMENTS $fragments")

        if (Util.SDK_INT >= 24 && fragments.isEmpty()) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()

        val fragments = supportFragmentManager.fragments
        Timber.d("FRAGMENTS $fragments")

        if ((Util.SDK_INT < 24 || exoPlayer == null) && fragments.isEmpty()) {
            initPlayer()
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }
        hideSystemUI()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.addOnBackStackChangedListener(this)

//        val video = when (challengeItem.id) {
//            103 -> R.raw.r103
//            104 -> R.raw.r104
//            105 -> R.raw.r105
//            106 -> R.raw.r106
//            107 -> R.raw.r107
//            108 -> R.raw.r108
//            else -> R.raw.r103
//        }

        binding.exoPlayer.setShowNextButton(false)
        binding.exoPlayer.setShowPreviousButton(false)

//        binding.videoView.setVideoURI(Uri.parse(challengeItem.videoUrl/*"android.resource://" + packageName + "/" + video*/))
//        binding.videoView.setOnPreparedListener {
//            binding.ivPlayReply.show()
//            binding.videoView.start()
//            binding.videoView.pause()
//            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
//        }
        /*binding.videoView.setOnInfoListener { mp, what, extra ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                //first frame was bufered - do your stuff here
                binding.ivPlayReply.invisible()
            }
            false
        }*/

//        binding.videoView.setPlayPauseListener(object :
//            StateBroadcastingVideoView.PlayPauseListener {
//            override fun onPlay() {
//                binding.ivPlayReply.invisible()
//            }
//
//            override fun onPause() {
//                binding.ivPlayReply.show()
//            }
//        })

        binding.ivPlayReply.onClick {
            closeSpotlight()
            if (exoPlayer?.playbackState == Player.STATE_ENDED) {
                exoPlayer?.seekTo(0);
            }
            exoPlayer?.playWhenReady = true;
            it.invisible()
        }

        binding.tvCancel.onClick { finish() }
        binding.tvNext.onClick {
            closeSpotlight()

            exoPlayer?.playWhenReady = false
            binding.ivPlayReply.show()

            val tag = ChallengeInstructionFragment.TAG
            replaceFragment(tag) {
                add(
                    R.id.container,
                    ChallengeInstructionFragment.getInstance(
                        challengeItem,
                        player
                    ),
                    tag
                ).addToBackStack(tag)
            }
        }

        startGuideIfNecessary()

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            exoPlayer?.playWhenReady = false
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.fragments.isEmpty()) {
                if (exoPlayer == null)
                    initPlayer()
            } else {
                if (exoPlayer != null)
                    releasePlayer()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private fun releasePlayer() {
        exoPlayer?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        exoPlayer = null
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
//                binding.videoView.start()
                exoPlayer?.playWhenReady = true
            }
        }
        binding.ivInfo.setOnClickListener {
//            binding.videoView.pause()
            exoPlayer?.playWhenReady = false
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
            "Play the video to see how the Routine should be completed. Then click Next to get instructions on setting yourself up for the Challenge"

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
