package com.mobile.praaktishockey.domain.common

import com.mobile.praaktishockey.ui.challenge.ChallengeVideoActivity
import com.mobile.praaktishockey.ui.challenge.DetailAnalysisFragment
import com.mobile.praaktishockey.ui.details.view.AnalysisFragment
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.mobile.praaktishockey.ui.main.view.DashboardFragment
import com.mobile.praaktishockey.ui.main.view.MenuFragment
import com.mobile.praaktishockey.ui.main.view.NewChallengeFragment
import com.mobile.praaktishockey.ui.timeline.view.TimelineItemFragment
import io.paperdb.Paper

object AppGuide {

    private const val APP_GUIDE_MAP = "APP_GUIDE_MAP"
    private const val DEVELOPER_MODE = false

    fun getGuideList(): HashMap<String, Boolean> {
        return Paper.book().read<HashMap<String, Boolean>>(APP_GUIDE_MAP, defaultMap)
    }

    private val defaultMap: HashMap<String, Boolean> = hashMapOf(
        Pair(DashboardFragment.TAG, false),
        Pair(NewChallengeFragment.TAG, false),
        Pair(AnalysisFragment.TAG, false),
        Pair(TimelineItemFragment.TAG, false),
        Pair(DetailAnalysisFragment.TAG, false),
        Pair(ChallengeInstructionFragment.TAG, false),
        Pair(ChallengeVideoActivity.TAG, false),
        Pair(MenuFragment.TAG, false)
    )

    fun setGuideDone(key: String) {
        val map = getGuideList()
        map[key] = true
        Paper.book().write(APP_GUIDE_MAP, map)
    }

    fun isGuideDone(key: String): Boolean {
        if (DEVELOPER_MODE)
            return false
        return getGuideList()[key] ?: false
    }

    fun clearGuides() {
        Paper.book().write(APP_GUIDE_MAP, defaultMap)
    }

}