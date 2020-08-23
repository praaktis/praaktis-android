package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LanguageItem(
    @SerializedName("key")
    val key: Int,
    @SerializedName("name")
    val name: String,
    @Transient
    val localeKey: String
) : Serializable