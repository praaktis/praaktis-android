package com.mobile.gympraaktis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "routine")
data class RoutineEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val imageUrl: String?,
): Serializable