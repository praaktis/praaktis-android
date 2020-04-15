package com.mobile.praaktishockey.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDTO(
    @SerializedName("userProfile_id")
    val userId: Int? = null,
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,
    @SerializedName("gender")
    val gender: Gender? = null,
    @SerializedName("ability")
    val ability: UserLevel? = null,
    @SerializedName("level")
    val level: Int? = null,
    @SerializedName("country")
    val country: Any? = null,
    @SerializedName("language")
    val language: Any? = null,
//        @SerializedName("age_group")
//        val ageGroup: Int? = null,
    @SerializedName("total_points")
    val totalPoints: Int? = null,
    @SerializedName("total_credits")
    val totalCredits: Int? = null,
    @SerializedName("praaktis_registered")
    val praaktisRegistered: Boolean? = null,
    @SerializedName("terms_accepted")
    val termsAccepted: Boolean? = null,
    @SerializedName("terms_accepted_date")
    val termsAcceptedDate: String? = null,
    @SerializedName("scaling_factor")
    val scalingFactor: Float? = null,
    @SerializedName("insert_point")
    val insertPoint: String? = null,
    @SerializedName("update_point")
    val updatePoint: String? = null,
    @SerializedName("state_indicator")
    val stateIndicator: Int? = null,
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
) : Serializable

enum class UserLevel(val label: String) {
    B("Beginner"),
    I("Intermediate"),
    E("Expert");
}

enum class Gender(val label: String) {
    M("Male"),
    F("Female")
}