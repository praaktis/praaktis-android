package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InstructionsDTO(
    @SerializedName("multiple")
    val multiple: List<String>? = emptyList(),
    @SerializedName("single")
    val single: List<String>? = emptyList()
): Serializable