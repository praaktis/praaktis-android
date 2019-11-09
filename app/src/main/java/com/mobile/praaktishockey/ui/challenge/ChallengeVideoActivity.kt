package com.mobile.praaktishockey.ui.challenge

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.replaceFragment
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.mobile.praaktishockey.ui.details.view.DetailsActivity
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import kotlinx.android.synthetic.main.activity_video_challenge.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChallengeVideoActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context, challengeItem: ChallengeItem) {
            val intent = Intent(context, ChallengeVideoActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            context.startActivity(intent)
        }
    }

    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as ChallengeItem }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_challenge)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.vid_drag_flick))
        videoView.setOnPreparedListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val playbackParams = PlaybackParams()
//                playbackParams.speed = 0.5f
                it.playbackParams = playbackParams
                ivPlayReply.hide()
            }
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
        }
        ivPlayReply.show()
        ivPlayReply.onClick {
            videoView.start()
            it.hide()
        }
        videoView.setOnCompletionListener {
            llCompleteVideo.show()
            ivPlayReply.show()
            ivPlayReply.setImageResource(R.drawable.vector_replay)
        }
        tvCancel.onClick { finish() }
        tvNext.onClick {
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
        Handler().postDelayed({
            videoView?.start()
        }, 2000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
