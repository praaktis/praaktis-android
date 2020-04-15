package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DashboardDTO(
        @SerializedName("total_points")
        val totalPoints: Long,
        @SerializedName("total_credits")
        val totalCredits: Long,
        @SerializedName("level")
        val level: Long,
        @SerializedName("points_to_next_level")
        val pointsToNextLevel: Long,
        @SerializedName("challenges")
        val challenges: List<AnalysisDTO>
) : Serializable