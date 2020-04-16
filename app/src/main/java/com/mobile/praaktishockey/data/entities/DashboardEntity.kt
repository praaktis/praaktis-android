package com.mobile.praaktishockey.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "dashboard")
data class DashboardEntity(
    @PrimaryKey
    val id: Int,
    val totalPoints: Long,
    val totalCredits: Long,
    val level: Long,
    val pointsToNextLevel: Long
) : Serializable

data class DashboardWithAnalysis(
    @Embedded
    val dashboard: DashboardEntity,
    @Relation(
        entity = AnalysisEntity::class,
        parentColumn = "id",
        entityColumn = "dashboardId"
    )
    val analysis: List<AnalysisComplete>
) : Serializable

