package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.praaktishockey.data.entities.AnalysisEntity
import com.mobile.praaktishockey.data.entities.AttemptChartDataEntity
import com.mobile.praaktishockey.data.entities.ChartDataEntity
import com.mobile.praaktishockey.data.entities.ScoreAnalysisEntity
import java.io.Serializable

data class AnalysisDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("max_score")
    val maxScore: Double,
    @SerializedName("avg_score")
    val averageScore: Double,
    @SerializedName("no_attempts")
    val noAttempts: Double,
    @SerializedName("attempt_chart")
    val attemptChart: AttemptChartData,
    @SerializedName("chart_data")
    val chartData: ChartData,
    @SerializedName("scores")
    val scores: List<ScoreDTO>
) : Serializable

data class AttemptChartData(
    @SerializedName("keys")
    val keys: List<String>,
    @SerializedName("series")
    val series: List<Double>
) : Serializable

fun AnalysisDTO.toAttemptChartDataEntity() =
    AttemptChartDataEntity(id, attemptChart.keys, attemptChart.series)

fun AnalysisDTO.toChartDataEntity() = ChartDataEntity(id, chartData.series, chartData.keys)

fun AnalysisDTO.toAnalysisEntity() = AnalysisEntity(id, name, 1, maxScore, averageScore, noAttempts)

