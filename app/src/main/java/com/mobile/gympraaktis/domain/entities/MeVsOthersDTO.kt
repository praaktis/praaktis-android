package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MeVsOthersDTO(
    @SerializedName("challenges")
    val challenges: List<MeVsOtherChallenge>
): Serializable