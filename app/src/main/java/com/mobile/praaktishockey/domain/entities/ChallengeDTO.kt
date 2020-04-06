package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChallengeDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val iconUrl: String?,
    @SerializedName("detail_points")
    val detailPoints: List<Challenge>
) : Serializable