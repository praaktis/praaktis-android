package com.mobile.gympraaktis.data.db

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
        scoreAnalysisList: List<ScoreAnalysisEntity>
    ) {
        insertDashboard(dashboardEntity)
        removeAllAnalysis()
        removeAllAttemptChartData()
        removeAllScoreAnalysis()
        insertAnalysis(list)
        insertAttemptChart(attemptChartList)
        insertChartData(chartDataList)
        insertScoreAnalysis(scoreAnalysisList)
    }

    @Transaction
    @Query("SELECT * FROM dashboard")
    fun getDashboardData(): Flow<DashboardWithAnalysis>

    @Transaction
    @Query("SELECT * FROM challenge_analysis")
    fun getChallengeAnalysis(): Flow<AnalysisComplete>

}