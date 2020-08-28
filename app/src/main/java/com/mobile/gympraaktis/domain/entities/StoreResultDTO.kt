package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreResultDTO(
    @SerializedName("userProfile_id")
    val userProfileId: Int,
    @SerializedName("challenge_id")
    val challengeId: Int,
    @SerializedName("time_performed")
    val timePerformed: String,
//    @SerializedName("outcome")
//    val outcome: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("points")
    val points: Int? = null,
    @SerializedName("score")
    val score: Float,
    @SerializedName("credits")
    val credits: Float? = null,
    @SerializedName("detail_result")
    val detailResult: List<DetailResult>,
    @SerializedName("video_id")
    val videoId: String? = null
) : Serializable