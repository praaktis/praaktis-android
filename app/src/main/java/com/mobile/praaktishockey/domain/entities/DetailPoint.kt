package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName

data class DetailPoint(
    @SerializedName("detail_point")
    val detailPoint: Int,
    @SerializedName("name")
    val name: String
)