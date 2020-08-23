package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.*
import com.mobile.gympraaktis.domain.common.Quadruple
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

fun DashboardDTO.toDashboardEntity() =
    DashboardEntity(1, totalPoints, totalCredits, level, pointsToNextLevel)

fun DashboardDTO.toAnalysisEntityList(): Quadruple<List<AnalysisEntity>, List<AttemptChartDataEntity>, List<ChartDataEntity>, List<ScoreAnalysisEntity>> {
    val analysisEntityList: MutableList<AnalysisEntity> = mutableListOf()
    val attemptChartDataEntityList: MutableList<AttemptChartDataEntity> = mutableListOf()
    val chartDataEntityList: MutableList<ChartDataEntity> = mutableListOf()
    val scoreAnalysisEntityList: MutableList<ScoreAnalysisEntity> = mutableListOf()

    challenges.forEach {
        analysisEntityList.add(it.toAnalysisEntity())
        attemptChartDataEntityList.add(it.toAttemptChartDataEntity())
        chartDataEntityList.add(it.toChartDataEntity())
        scoreAnalysisEntityList.addAll(it.scores.map { score -> score.toScoreAnalysisEntity(it.id) })
    }
    return Quadruple(
        analysisEntityList,
        attemptChartDataEntityList,
        chartDataEntityList,
        scoreAnalysisEntityList
    )
}


