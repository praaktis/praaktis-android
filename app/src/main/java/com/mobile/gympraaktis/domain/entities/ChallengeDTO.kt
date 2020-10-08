package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChallengeDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val iconUrl: String?,
    @SerializedName("instructions")
    val instructions: InstructionsDTO?
) : Serializable