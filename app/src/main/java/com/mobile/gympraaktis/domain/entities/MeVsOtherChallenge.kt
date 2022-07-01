package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MeVsOtherChallenge(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("attempt_chart")
    val attemptChart: AttemptChart,
    @SerializedName("avg_score")
    val avgScore: Double,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("low_score")
    val lowScore: Double,
    @SerializedName("max_score")
    val maxScore: Double,
    @SerializedName("chart_data")
    val chartData: ChartData,
    @SerializedName("leaderboard")
    val leaderboard: List<Leader>
) : Serializable

data class AttemptChart(
    @SerializedName("me")
    val me: Int,
    @SerializedName("others")
    val others: Int
) : Serializable

data class Leader(
    @SerializedName("playerid")
    val userId: Int,
    @SerializedName("playername")
    val playerName: String,
    @SerializedName("nickname")
    val nickName: String,
    @SerializedName("max_score")
    val maxScore: Double,
    @SerializedName("image_url")
    val imageUrl: String?
) : Serializable