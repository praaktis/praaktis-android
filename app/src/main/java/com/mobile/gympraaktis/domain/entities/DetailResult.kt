package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailResult(
    @SerializedName("detail_point_id")
    val detailPointId: Int,
    @SerializedName("detail_point_score")
    val detailPointScore: Float
) : Serializable