package com.mobile.gympraaktis.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mobile.gympraaktis.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDashboard(dashboardEntity: DashboardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnalysis(analysisEntity: List<AnalysisEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttemptChart(attemptChartDataEntity: List<AttemptChartDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChartData(chartData: List<ChartDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScoreAnalysis(score: List<ScoreAnalysisEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayers(list: List<PlayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayer(list: PlayerEntity)

    @Update
    fun updatePlayer(list: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoutines(list: List<RoutineEntity>)

    @Query("DELETE FROM player")
    fun removeAllPlayers()

    @Query("DELETE FROM challenge_analysis")
    fun removeAllAnalysis()

    @Query("DELETE FROM attempt_chartData")
    fun removeAllAttemptChartData()

    @Query("DELETE FROM chartData")
    fun removeAllChartData()

    @Query("DELETE FROM score_analysis")
    fun removeAllScoreAnalysis()

    @Transaction
    fun setDashboardData(
        dashboardEntity: DashboardEntity,
        list: List<AnalysisEntity>,
        attemptChartList: List<AttemptChartDataEntity>,
        chartDataList: List<ChartDataEntity>,
        scoreAnalysisList: List<ScoreAnalysisEntity>,
        players: List<PlayerEntity>
    ) {
        insertDashboard(dashboardEntity)
        removeAllPlayers()
        removeAllAnalysis()
        removeAllAttemptChartData()
        removeAllScoreAnalysis()
        insertPlayers(players)
        insertAnalysis(list)
        insertAttemptChart(attemptChartList)
        insertChartData(chartDataList)
        insertScoreAnalysis(scoreAnalysisList)
    }

    @Query("SELECT * FROM player")
    fun getPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM routine")
    fun getRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routine")
    fun getRoutinesLiveData(): LiveData<List<RoutineEntity>>

    @Query("SELECT * FROM dashboard")
    fun getDashboardData(): LiveData<DashboardEntity>

    @Transaction
    @Query("SELECT * FROM challenge_analysis")
    fun getAllAnalysisData(): Flow<List<AnalysisComplete>>

    @Transaction
    @Query("SELECT * FROM player")
    fun getPlayersAnalysis(): Flow<List<PlayerAnalysis>>

    @Transaction
    @Query("SELECT * FROM routine")
    fun getRoutineAnalysis(): Flow<List<RoutineAnalysis>>

    @Transaction
    @Query("SELECT * FROM challenge_analysis")
    fun getChallengeAnalysis(): Flow<AnalysisComplete>


}