package com.mobile.gympraaktis.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.praaktis.exerciseengine.Engine.Outputs.Measurement
import java.io.Serializable

@Entity(
    tableName = "offline_exercise_result",
    primaryKeys = ["time_performed", "routine_id", "player_id"]
)
data class StoreResultModel(
    @Transient
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @SerializedName("player_id")
    @ColumnInfo(name = "player_id")
    val userProfileId: Long,
    @SerializedName("routine_id")
    @ColumnInfo(name = "routine_id")
    val challengeId: Int,
    @SerializedName("time_performed")
    @ColumnInfo(name = "time_performed")
    val timePerformed: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("points")
    val points: Int? = null,
    @SerializedName("score")
    val score: Float,
    @SerializedName("credits")
    val credits: Float? = null,
    @SerializedName("detail_result")
    val detailResult: List<DetailResult>,
    @SerializedName("video_id")
    val videoId: String? = null,
    @SerializedName("measurements")
    val measurements: List<Measurement>
) : Serializable
