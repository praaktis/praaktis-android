package com.mobile.praaktishockey.ui.challenge

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.data.entities.TimelineEntity
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.setLightNavigationBar
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.domain.extension.transparentStatusAndNavigationBar
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment

class ChallengeActivity constructor(override val layoutId: Int = R.layout.activity_challenge) :
    BaseActivity() {

    companion object {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("_RESULT", "REQUEST: " + requestCode)
        if (requestCode == 333) {
            finish()
            if (resultCode == Activity.RESULT_OK) {
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
            } else {
                Log.d("__RESULT", "Result NOT OK $resultCode")
                Log.d("__RESULT", "Result NOT OK ${data?.getSerializableExtra("result")}")
            }
        }
    }
}