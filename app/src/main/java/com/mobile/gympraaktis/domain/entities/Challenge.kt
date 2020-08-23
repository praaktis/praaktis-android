package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Challenge (
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String
) : Serializable