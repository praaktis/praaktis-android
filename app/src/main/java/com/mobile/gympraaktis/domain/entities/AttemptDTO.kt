package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.AttemptEntity
import java.io.Serializable

data class AttemptDTO(
    @SerializedName("id")
    val attemptId: Int,
    @SerializedName("challenge")
    val challenge: ChallengeDTO,
    @SerializedName("time_performed")
    val timePerformed: String,
    @SerializedName("score")
    val score: Float,
    @SerializedName("points")
    val points: Int
) : Serializable

fun AttemptDTO.toEntity(page: Int): AttemptEntity =
    AttemptEntity(attemptId, timePerformed, score, points, challenge.name, challenge.id, page)