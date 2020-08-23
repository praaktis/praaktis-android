package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ComparisonDTO(
    @SerializedName("gender")
    val gender: String,
    @SerializedName("age_group")
    val ageGroup: String,
    @SerializedName("ability")
    val ability: String,
    @SerializedName("others")
    val others: MeVsOthersDTO,
    @SerializedName("friends")
    val friends: MeVsOthersDTO
) : Serializable