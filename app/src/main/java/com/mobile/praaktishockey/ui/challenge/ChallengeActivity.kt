package com.mobile.praaktishockey.ui.challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.login.view.CalibrateFragment
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem

class ChallengeActivity constructor(override val layoutId: Int = R.layout.activity_challenge)
    : BaseActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            //todo
            activity.startActivity(intent)
        }

        fun start(activity: Activity, challengeItem: ChallengeItem) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("challengeItem", challengeItem)
            activity.startActivity(intent)
        }

        fun start(activity: Activity, scoreDTO: ScoreDTO) {
            val intent = Intent(activity, ChallengeActivity::class.java)
            intent.putExtra("score", scoreDTO)
            activity.startActivity(intent)
        }
    }

    private val challengeItem by lazy { intent.getSerializableExtra("challengeItem") as ChallengeItem }

    override fun initUI(savedInstanceState: Bundle?) {
        if (intent.hasExtra("score")) {
            val tag = DetailAnalysisFragment.TAG
            showOrReplace(tag) {
                add(R.id.container, DetailAnalysisFragment.getInstance(intent.getSerializableExtra("score") as ScoreDTO), tag)
            }
        } else if(intent.hasExtra("challengeItem")){
            val tag = CalculateChallengeFragment.TAG
            showOrReplace(tag) {
                add(R.id.container, CalculateChallengeFragment.getInstance(challengeItem), tag)
            }
        } else {
            val tag = CalibrateFragment.TAG
            showOrReplace(tag) {
                add(
                    R.id.container,
                    CalibrateFragment.getInstance(false),
                    tag
                )
            }
        }
    }
}