package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.AnalysisEntity
import com.mobile.gympraaktis.data.entities.AttemptChartDataEntity
import com.mobile.gympraaktis.data.entities.ChartDataEntity
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

fun AnalysisDTO.toAttemptChartDataEntity(playerId: Long) =
    AttemptChartDataEntity(id, attemptChart.keys, attemptChart.series, playerId, "${id}_${playerId}")

fun AnalysisDTO.toChartDataEntity(playerId: Long) =
    ChartDataEntity(id, chartData.series, chartData.keys, playerId, "${id}_${playerId}")

fun AnalysisDTO.toAnalysisEntity(playerId: Long) =
    AnalysisEntity(id, playerId, name, 1, maxScore, averageScore, noAttempts, "${id}_${playerId}")

