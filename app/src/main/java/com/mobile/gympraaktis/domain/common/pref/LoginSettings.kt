package com.mobile.gympraaktis.domain.common.pref

import com.mobile.gympraaktis.domain.entities.*

interface LoginSettings {
    fun isLoggedIn(): Boolean
    fun token(): String
    fun unsafeToken(): String?
    fun logout()

    fun isShowedIntroPage(): Boolean
    fun getLanguage(): String
    fun getProfile(): UserDTO?
    fun setProfile(userDTO: UserDTO)
    fun getChallenges(): List<ChallengeDTO>?
    fun setChallenges(challenges: List<ChallengeDTO>)
    fun getDashboard(): DashboardDTO?
    fun setDashboard(dashboardDTO: DashboardDTO)
    fun setTimeline(timelineDTO: TimelineDTO)
    fun getTimeline(): TimelineDTO?
    fun setTimelineDetails(timelineDetails: List<DetailScoreDTO>, attemptId: Int)
    fun getTimelineDetails(attemptId: Int): List<DetailScoreDTO>?
    fun setComparison(comparisonDTO: ComparisonDTO)
    fun getComparison(): ComparisonDTO?

    fun cameraMode(): Boolean
    fun praaktisServerName(): String
}