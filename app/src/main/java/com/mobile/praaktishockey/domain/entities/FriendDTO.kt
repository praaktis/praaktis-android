package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FriendDTO(
    @SerializedName("friend_email")
    val friendEmail: String,
    @SerializedName("friend_fname")
    val friendFirstName: String,
    @SerializedName("friend_lname")
    val friendLastName: String,
    @SerializedName("friend_status")
    val friendStatus: String,
    @SerializedName("requested_by")
    val requestedBy: String,
    @SerializedName("id")
    val id: Int
) : Serializable