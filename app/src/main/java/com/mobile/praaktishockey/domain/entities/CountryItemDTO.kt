package com.mobile.praaktishockey.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "countries")
data class CountryItemDTO(
    @SerializedName("key")
    @PrimaryKey
    val key: String,
    @SerializedName("name")
    val name: String
)