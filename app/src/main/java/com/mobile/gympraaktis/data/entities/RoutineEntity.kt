package com.mobile.gympraaktis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobile.gympraaktis.domain.entities.DetailPoint
import java.io.Serializable

@Entity(tableName = "routine")
data class RoutineEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val instructionsMultiple: List<String>?,
    val instructionsSingle: List<String>?,
    val videoGuide: List<String>?,
    val videoUrl: String?,
    val downloadDate: String?,
    val detailPoint: List<DetailPoint>?,
) : Serializable