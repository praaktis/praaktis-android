package com.mobile.gympraaktis.data.entities

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "challenge_analysis")
data class AnalysisEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val dashboardId: Int,
    val maxScore: Double,
    val averageScore: Double,
    val noAttempts: Double
) : Serializable

@Entity(tableName = "attempt_chartData")
data class AttemptChartDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val keys: List<String>,
    val series: List<Double>
) : Serializable

@Entity(tableName = "chartData")
data class ChartDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val series: List<Double>,
    val keys: List<Int>
) : Serializable

@Entity(tableName = "score_analysis")
data class ScoreAnalysisEntity(
    @PrimaryKey
    @ColumnInfo(name = "attempt_id")
    val attemptId: Int,
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val score: Double,
    @ColumnInfo(name = "time_performed")
    val timePerformed: String?
) : Serializable

data class AnalysisComplete(
    @Embedded
    val analysisEntity: AnalysisEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val attemptChart: AttemptChartDataEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val chartData: ChartDataEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val score: List<ScoreAnalysisEntity>
) : Serializable