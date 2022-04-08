package com.mobile.gympraaktis.ui.challenge

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.data.entities.AttemptEntity
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.databinding.ActivityChallengeBinding
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.extension.materialAlert
import com.mobile.gympraaktis.domain.extension.setLightNavigationBar
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.domain.extension.transparentStatusAndNavigationBar
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.mobile.gympraaktis.ui.login.view.LoginActivity
import com.mobile.gympraaktis.ui.main.vm.MenuViewModel
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import timber.log.Timber

class ChallengeActivity constructor(override val layoutId: Int = R.layout.activity_challenge) :
    BaseActivity<ActivityChallengeBinding>() {

    companion object {
        const val PRAAKTIS_SDK_REQUEST_CODE = 333

        fun start(
            activity: Activity,
            challengeItem: ChallengeDTO,
            result: HashMap<String, Any>?,
            path: String?,
            pathTest: String?,
            videoId: String?,
            player: PlayerEntity,
        ) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            intent.putExtra(ChallengeInstructionFragment.RAW_VIDEO_PATH, path)
            intent.putExtra(ChallengeInstructionFragment.VIDEO_PATH, pathTest)
            intent.putExtra(ChallengeInstructionFragment.VIDEO_ID, videoId)
            intent.putExtra("player", player)
            if (result != null)
                intent.putExtra(ChallengeInstructionFragment.CHALLENGE_RESULT, result)
            activity.startActivity(intent)
        }

        fun start(activity: Activity, attemptEntity: AttemptEntity) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("score", attemptEntity)
            activity.startActivity(intent)
        }

    }

    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as ChallengeDTO }
    private val player by lazy { intent.getSerializableExtra("player") as PlayerEntity }

    override val mViewModel: MenuViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }

        if (intent.hasExtra("score")) {
            val tag = DetailAnalysisFragment.TAG
            showOrReplace(tag) {
                add(
                    R.id.container,
                    DetailAnalysisFragment.getInstance(intent.getSerializableExtra("score") as AttemptEntity),
                    tag
                )
            }
        } else if (intent.hasExtra("challengeItem")) {
            val tag = ResultChallengeFragment.TAG
            showOrReplace(tag) {
                replace(
                    R.id.container,
                    ResultChallengeFragment.getInstance(challengeItem, player), tag
                )
            }
        }

        mViewModel.logoutEvent.observe(this, Observer {
            if (it) {
                Log.d("HERELOGOUT", "LOGOUT")
                mViewModel.onLogoutSuccess()

                LoginActivity.startAndFinishAll(this)
                this.finish()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PRAAKTIS_SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    finish()
                    Timber.d("RESULT_OK")
                    @Suppress("UNCHECKED_CAST")
                    start(
                        this,
                        challengeItem,
                        data!!.getSerializableExtra("result") as HashMap<String, Any>?,
                        data.getStringExtra(
                            ChallengeInstructionFragment.RAW_VIDEO_PATH
                        ),
                        data.getStringExtra(ChallengeInstructionFragment.VIDEO_PATH),
                        data.getStringExtra(ChallengeInstructionFragment.VIDEO_ID),
                        player,
                    )
                }
                ExerciseEngineActivity.AUTHENTICATION_FAILED -> {
                    Timber.d("LOGOUT EVENT : AUTHENTICATION_FAILED")
                    mViewModel.logout()
                }
                ExerciseEngineActivity.CALIBRATION_FAILED -> {
                    showErrorDialog("Calibration failed, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.POOR_CONNECTION -> {
                    showErrorDialog("Poor connection, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.CANNOT_REACH_SERVER -> {
                    showErrorDialog("Cannot reach server, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.SMTH_WENT_WRONG -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
                Activity.RESULT_CANCELED -> {

                }
                else -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        materialAlert {
            setCancelable(false)
            setMessage(message)
            setPositiveButton(R.string.try_again) { dialog, which ->
                val fragment = supportFragmentManager.findFragmentById(R.id.container)
                if (fragment is ResultChallengeFragment) {
                    fragment.startExercise()
                }
            }
            setNegativeButton(R.string.cancel) { dialog, which ->
                finish()
            }
        }.show()
    }

}