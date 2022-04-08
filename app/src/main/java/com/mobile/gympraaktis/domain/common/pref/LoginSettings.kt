package com.mobile.gympraaktis.domain.common.pref

import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.entities.ComparisonDTO
import com.mobile.gympraaktis.domain.entities.DetailScoreDTO
import com.mobile.gympraaktis.domain.entities.UserDTO

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
    fun setTimelineDetails(timelineDetails: List<DetailScoreDTO>, attemptId: Int)
    fun getTimelineDetails(attemptId: Int): List<DetailScoreDTO>?
    fun setComparison(comparisonDTO: ComparisonDTO)
    fun getComparison(): ComparisonDTO?

    fun cameraMode(): Boolean

    fun getSelectedPlayerId(): Long
    fun setSelectedPlayerId(playerId: Long)
}