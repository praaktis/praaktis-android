package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimelineChallengeItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("latest")
    val latest: ScoreDTO,
    @SerializedName("scores")
    val scores: List<ScoreDTO>
) : Serializable