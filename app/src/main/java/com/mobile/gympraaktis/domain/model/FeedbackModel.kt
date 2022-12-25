package com.mobile.gympraaktis.domain.model

import com.google.gson.annotations.SerializedName

data class FeedbackModel(
    @SerializedName("routine_id")
    val id: Int,
    val name: String,
    @SerializedName("pose_info")
    val poseInfo: String,
    @SerializedName("feedback_score")
    val feedbackScore: Int
)
