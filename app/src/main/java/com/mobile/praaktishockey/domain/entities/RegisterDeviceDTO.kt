package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterDeviceDTO (
    @SerializedName("device_id")
    val deviceId: String
): Serializable