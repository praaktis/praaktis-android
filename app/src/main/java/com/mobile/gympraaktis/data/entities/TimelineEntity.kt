package com.mobile.gympraaktis.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "timeline")
data class TimelineEntity(
    @PrimaryKey
    @ColumnInfo(name = "attempt_id")
    val attemptId: Int,
    val points: Int,
    val score: Double,
    @ColumnInfo(name = "time_performed")
    val timePerformed: String?,
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    @ColumnInfo(name = "challenge_name")
    val challengeName: String
) : Serializable
