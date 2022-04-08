package com.mobile.gympraaktis.domain.common

import com.mobile.gympraaktis.ui.challenge.ChallengeVideoActivity
import com.mobile.gympraaktis.ui.challenge.DetailAnalysisFragment
import com.mobile.gympraaktis.ui.details.view.AnalysisFragment
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.mobile.gympraaktis.ui.friends.view.FriendsPagerFragment
import com.mobile.gympraaktis.ui.main.view.DashboardFragment
import com.mobile.gympraaktis.ui.main.view.MenuFragment
import com.mobile.gympraaktis.ui.main.view.NewChallengeFragment
import com.mobile.gympraaktis.ui.timeline.view.TimelineItemFragment
import io.paperdb.Paper

object AppGuide {

    private const val APP_GUIDE_MAP = "APP_GUIDE_MAP"
    private const val DEVELOPER_MODE = false

    fun getGuideList(): HashMap<String, Boolean> {
        return Paper.book().read<HashMap<String, Boolean>>(APP_GUIDE_MAP, defaultMap).orEmpty() as HashMap<String, Boolean>
    }

    private val defaultMap: HashMap<String, Boolean> = hashMapOf(
        Pair(DashboardFragment.TAG, false),
        Pair(NewChallengeFragment.TAG, false),
        Pair(AnalysisFragment.TAG, false),
        Pair(TimelineItemFragment.TAG, false),
        Pair(DetailAnalysisFragment.TAG, false),
        Pair(ChallengeInstructionFragment.TAG, false),
        Pair(ChallengeVideoActivity.TAG, false),
        Pair(MenuFragment.TAG, false),
        Pair(FriendsPagerFragment.TAG, false)
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