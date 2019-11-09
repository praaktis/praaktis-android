package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailScoreDTO(
    @SerializedName("detail_point")
    val detailPoint: DetailPoint,
    @SerializedName("detail_point_score")
    val detailPointScore: Double
) : Serializable