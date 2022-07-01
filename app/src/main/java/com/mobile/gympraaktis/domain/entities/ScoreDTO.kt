package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.ScoreAnalysisEntity
import java.io.Serializable

data class ScoreDTO(
    @SerializedName("attempt_id")
    val attemptId: Int,
    @SerializedName("time_performed")
    val timePerformed: String?,
    @SerializedName("score")
    val score: Double
) : Serializable

fun ScoreDTO.toScoreAnalysisEntity(challengeId: Int, playerId: Long) =
    ScoreAnalysisEntity(attemptId, challengeId, score, timePerformed, playerId, "${challengeId}_${playerId}")
