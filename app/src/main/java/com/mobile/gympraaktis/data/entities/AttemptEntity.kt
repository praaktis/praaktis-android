package com.mobile.gympraaktis.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "attempt")
data class AttemptEntity(
    @PrimaryKey
    @ColumnInfo(name = "attempt_id")
    val attemptId: Int,
    @ColumnInfo(name = "time_performed")
    val timePerformed: String,
    val score: Float,
    val points: Int,
    @ColumnInfo(name = "challenge_name")
    val challengeName: String,
    @ColumnInfo(name = "challenge_id")
    val challengeId: Int,
    val page: Int
) : Serializable