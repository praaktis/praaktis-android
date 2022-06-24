package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName

data class DetailPoint(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("max_value")
    val maxValue: Float,
)