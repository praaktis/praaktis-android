package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.praaktishockey.data.entities.ScoreAnalysisEntity
import com.mobile.praaktishockey.data.entities.TimelineEntity
import java.io.Serializable

data class ScoreDTO(
    @SerializedName("attempt_id")
    val attemptId: Int,
    @SerializedName("time_performed")
    val timePerformed: String?,
    @SerializedName("name")
    var name: String,
    @SerializedName("points")
    val points: Int,
    @SerializedName("score")
    val score: Double
) : Serializable

fun ScoreDTO.toScoreAnalysisEntity(challengeId: Int) =
    ScoreAnalysisEntity(attemptId, challengeId, score, timePerformed)

fun ScoreDTO.toTimelineEntity(challengeId: Int, challengeName: String) =
    TimelineEntity(attemptId, points, score, timePerformed, challengeId, challengeName)