package com.mobile.praaktishockey.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class UserData(
    @SerializedName("id")
    @PrimaryKey
    val id: Long
)