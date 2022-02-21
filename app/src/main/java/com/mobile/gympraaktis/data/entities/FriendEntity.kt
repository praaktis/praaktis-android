package com.mobile.gympraaktis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val status: String,
    val requestedBy: String?,
    val imageUrl: String,
) : Serializable