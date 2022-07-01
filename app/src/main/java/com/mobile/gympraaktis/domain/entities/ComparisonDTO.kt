package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ComparisonDTO(
    @SerializedName("gender")
    val gender: GenderDTO,
    @SerializedName("age_group")
    val ageGroup: KeyValueDTO,
    @SerializedName("ability")
    val ability: AbilityDTO,
    @SerializedName("routines")
    val routines: List<MeVsOtherChallenge>,
) : Serializable