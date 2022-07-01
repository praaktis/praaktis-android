package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimelineDTO(
    @SerializedName("challenges")
    val challenges: ArrayList<TimelineChallengeItem>
) : Serializable
