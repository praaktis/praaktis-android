package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InviteFriendRequest(
    @SerializedName("invited_email")
    val inviteEmail: String
) : Serializable

data class ConfirmFriendRequest(
    @SerializedName("friend_email")
    val inviteEmail: String
) : Serializable

data class DeleteFriendRequest(
    @SerializedName("friend_email")
    val email: String
): Serializable

data class UserMessage(
    @SerializedName("message")
    val message: String
) : Serializable