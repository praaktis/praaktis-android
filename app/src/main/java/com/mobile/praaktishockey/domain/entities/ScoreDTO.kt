package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
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