package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateUserDTO(
        @SerializedName("email")
        val userName: String,
        @SerializedName("password")
        val password: String) : Serializable