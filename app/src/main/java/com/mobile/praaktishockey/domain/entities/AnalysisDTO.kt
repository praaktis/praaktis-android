package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AnalysisDTO(
        @SerializedName("name")
        val name: String,
        @SerializedName("max_score")
        val maxScore: Double,
        @SerializedName("avg_score")
        val averageScore: Double,
        @SerializedName("no_attempts")
        val no_attempts: Double,
        @SerializedName("attempt_chart")
        val attemptChart: AttemptChartData,
        @SerializedName("chart_data")
        val chartData: ChartData,
        @SerializedName("scores")
        val scores: Array<ScoreDTO>
): Serializable

data class AttemptChartData(
        @SerializedName("keys")
        val keys: List<String>,
        @SerializedName("series")
        val series: List<Double>
) : Serializable