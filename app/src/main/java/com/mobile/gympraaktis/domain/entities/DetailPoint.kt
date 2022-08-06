package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailPoint(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name", alternate = ["label"])
    val name: String,
    @SerializedName("max_value")
    val maxValue: Float,
    @SerializedName("help_text")
    val helpText: String?,
) : Serializable