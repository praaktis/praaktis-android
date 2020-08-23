package com.mobile.gympraaktis.ui.challenge

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.data.entities.TimelineEntity
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.extension.getViewModel
import com.mobile.gympraaktis.domain.extension.setLightNavigationBar
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.domain.extension.transparentStatusAndNavigationBar
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.mobile.gympraaktis.ui.login.view.LoginActivity
import com.mobile.gympraaktis.ui.main.vm.MenuViewModel
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import timber.log.Timber

class ChallengeActivity constructor(override val layoutId: Int = R.layout.activity_challenge) :
    BaseActivity() {

    companion object {
        const val PRAAKTIS_SDK_REQUEST_CODE = 333

        fun start(activity: Activity) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            //todo
            activity.startActivity(intent)
        }

        fun start(
            activity: Activity,
            challengeItem: ChallengeDTO,
            result: HashMap<String, Any>?,
            path: String,
            pathTest: String?
        ) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            intent.putExtra(ChallengeInstructionFragment.RAW_VIDEO_PATH, path)
            intent.putExtra(ChallengeInstructionFragment.VIDEO_PATH, pathTest)
            if (result != null)
                intent.putExtra(ChallengeInstructionFragment.CHALLENGE_RESULT, result)
            activity.startActivity(intent)
        }

        fun start(activity: Activity, scoreDTO: TimelineEntity) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("score", scoreDTO)
            activity.startActivity(intent)
        }
    }

    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as ChallengeDTO }

    override val mViewModel: MenuViewModel get() = getViewModel { MenuViewModel(application) }

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
                    DetailAnalysisFragment.getInstance(intent.getSerializableExtra("score") as TimelineEntity),
                    tag
                )
            }
        } else if (intent.hasExtra("challengeItem")) {
            val tag = ResultChallengeFragment.TAG
            showOrReplace(tag) {
                replace(
                    R.id.container,
                    ResultChallengeFragment.getInstance(challengeItem), tag
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
        Log.d("_RESULT", "REQUEST: " + requestCode)
        if (requestCode == PRAAKTIS_SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    finish()
                    Log.d("_RESULT", "RESULT_OK")
                    @Suppress("UNCHECKED_CAST")
                    start(
                        this,
                        challengeItem,
                        data!!.getSerializableExtra("result") as HashMap<String, Any>?,
                        data.getStringExtra(
                            ChallengeInstructionFragment.RAW_VIDEO_PATH
                        ),
                        data.getStringExtra(ChallengeInstructionFragment.VIDEO_PATH)
                    )
                }
                ExerciseEngineActivity.AUTHENTICATION_FAILED -> {
                    Timber.d("LOGOUT EVENT : AUTHENTICATION_FAILED")
                    mViewModel.logout()
                }
                ExerciseEngineActivity.CALIBRATION_FAILED -> {
                    finish()
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.POOR_CONNECTION -> {
                    finish()
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                else -> {
                    finish()
                    Log.d("__RESULT", "Result NOT OK $resultCode")
                    Log.d("__RESULT", "Result NOT OK ${data?.getSerializableExtra("result")}")
                }
            }
        }
    }
}