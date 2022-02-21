package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.FriendEntity
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
    val requestedBy: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("friend_image")
    val friendImage: String
) : Serializable

fun FriendDTO.toEntity(): FriendEntity {
    return FriendEntity(
        friendEmail,
        friendFirstName,
        friendLastName,
        "$friendFirstName $friendLastName",
        friendStatus,
        requestedBy,
        friendImage
    )
}
