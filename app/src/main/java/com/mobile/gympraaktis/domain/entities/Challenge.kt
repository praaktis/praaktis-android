package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoutineDTO(
    @SerializedName("routine_id")
    val id: Long,
    @SerializedName("name")
    val name: String
) : Serializable