package com.mobile.gympraaktis.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "dashboard")
data class DashboardEntity(
    @PrimaryKey
    val id: Int,
    val allowedPlayers: Long,
    val activePlayers: Long,
    val level: Long,
    val attemptsAvailable: Long,
    val recordedAttempts: Long,
) : Serializable

data class DashboardWithPlayers(
    @Embedded
    val dashboard: PlayerAnalysis,
    @Relation(
        entity = PlayerEntity::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val analysis: List<PlayerAnalysis>
) : Serializable
