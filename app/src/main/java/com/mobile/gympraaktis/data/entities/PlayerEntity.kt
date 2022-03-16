package com.mobile.gympraaktis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobile.gympraaktis.domain.entities.PlayerAnalysisDTO
import java.io.Serializable

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
) : Serializable

fun PlayerAnalysisDTO.toPlayerEntity(): PlayerEntity {
    return PlayerEntity(playerId, playerName)
}