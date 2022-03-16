package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.*
import com.mobile.gympraaktis.domain.common.Quintuple
import java.io.Serializable

data class DashboardDTO(
    @SerializedName("allowed_players")
    val allowedPlayers: Long,
    @SerializedName("activated_players")
    val activatedPlayers: Long,
    @SerializedName("level")
    val level: Long,
    @SerializedName("videos_available")
    val videosAvailable: Long,
    @SerializedName("videos_recorded")
    val videosRecorded: Long,
    @SerializedName("routines")
    val routines: List<RoutineDTO>,
    @SerializedName("players")
    val players: List<PlayerAnalysisDTO>

) : Serializable

fun DashboardDTO.toDashboardEntity() =
    DashboardEntity(1, allowedPlayers, activatedPlayers, level, videosAvailable, videosRecorded)

fun RoutineDTO.toRoutineEntity() = RoutineEntity(id, name, null)

fun DashboardDTO.toAnalysisEntityList(): Quintuple<List<AnalysisEntity>, List<AttemptChartDataEntity>, List<ChartDataEntity>, List<ScoreAnalysisEntity>, List<PlayerEntity>> {
    val analysisEntityList: MutableList<AnalysisEntity> = mutableListOf()
    val attemptChartDataEntityList: MutableList<AttemptChartDataEntity> = mutableListOf()
    val chartDataEntityList: MutableList<ChartDataEntity> = mutableListOf()
    val scoreAnalysisEntityList: MutableList<ScoreAnalysisEntity> = mutableListOf()
    val playerEntityList: MutableList<PlayerEntity> = mutableListOf()

    players.forEach { player ->
        playerEntityList.add(player.toPlayerEntity())
        analysisEntityList.addAll(player.analysis.map { it.toAnalysisEntity(player.playerId) })
        attemptChartDataEntityList.addAll(player.analysis.map { it.toAttemptChartDataEntity(player.playerId) })
        chartDataEntityList.addAll(player.analysis.map { it.toChartDataEntity(player.playerId) })
        player.analysis.forEach {
            scoreAnalysisEntityList.addAll(it.scores.map { score ->
                score.toScoreAnalysisEntity(
                    it.id,
                    player.playerId
                )
            })
        }
    }

    return Quintuple(
        analysisEntityList,
        attemptChartDataEntityList,
        chartDataEntityList,
        scoreAnalysisEntityList,
        playerEntityList,
    )
}


