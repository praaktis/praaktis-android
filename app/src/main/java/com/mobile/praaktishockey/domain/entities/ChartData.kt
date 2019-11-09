package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChartData(
    @SerializedName("series")
    val series: List<Double>,
    @SerializedName("keys")
    val keys: List<Int>
): Serializable