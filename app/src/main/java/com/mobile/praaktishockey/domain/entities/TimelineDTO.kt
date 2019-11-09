package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimelineDTO (
    @SerializedName("challenges")
    val challenges: ArrayList<TimelineChallengeItem>
): Serializable