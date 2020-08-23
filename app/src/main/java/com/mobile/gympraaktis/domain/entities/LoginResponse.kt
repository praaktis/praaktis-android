package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("praaktis_registered")
    val praaktisRegistered: String,
    @SerializedName("terms_accepted")
    val termsAccepted: String,
    @SerializedName("scaling_factor")
    val scalingFactor: String,
    @SerializedName("insert_point")
    val insertPoint: String,
    @SerializedName("update_point")
    val updatePoint: String,
    @SerializedName("state_indicator")
    val stateIndicator: String
) : Serializable