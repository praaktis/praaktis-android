package com.mobile.gympraaktis.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "challenge_analysis", primaryKeys = ["id", "playerId"])
data class AnalysisEntity(
    val id: Int,
    val playerId: Long,
    val name: String,
    val dashboardId: Int,
    val maxScore: Double,
    val averageScore: Double,
    val noAttempts: Double
) : Serializable

@Entity(tableName = "attempt_chartData", primaryKeys = ["challenge_id", "playerId"])
data class AttemptChartDataEntity(
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val keys: List<String>,
    val series: List<Double>,
    val playerId: Long,
) : Serializable

@Entity(tableName = "chartData", primaryKeys = ["challenge_id", "playerId"])
data class ChartDataEntity(
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val series: List<Double>,
    val keys: List<Int>,
    val playerId: Long,
) : Serializable

@Entity(tableName = "score_analysis", primaryKeys = ["attempt_id", "playerId"])
data class ScoreAnalysisEntity(
    @ColumnInfo(name = "attempt_id")
    val attemptId: Int,
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val score: Double,
    @ColumnInfo(name = "time_performed")
    val timePerformed: String?,
    val playerId: Long,
) : Serializable

data class AnalysisComplete(
    @Embedded
    val analysisEntity: AnalysisEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val attemptChart: AttemptChartDataEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val chartData: ChartDataEntity,
    @Relation(parentColumn = "id", entityColumn = "challenge_id")
    val score: List<ScoreAnalysisEntity>,
    @Relation(parentColumn = "playerId", entityColumn = "id")
    val playerEntity: PlayerEntity,
) : Serializable

data class PlayerAnalysis(
    @Embedded
    val playerEntity: PlayerEntity,
    @Relation(entity = AnalysisEntity::class, parentColumn = "id", entityColumn = "playerId")
    val analysisComplete: List<AnalysisComplete>
) : Serializable

data class RoutineAnalysis(
    @Embedded
    val routineEntity: RoutineEntity,
    @Relation(
        entity = AnalysisEntity::class,
        parentColumn = "id",
        entityColumn = "id",
    )
    val analysis: List<AnalysisComplete>
): Serializable
