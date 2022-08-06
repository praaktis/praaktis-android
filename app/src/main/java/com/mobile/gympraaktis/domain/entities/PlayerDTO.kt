package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlayerAnalysisDTO(
    @SerializedName("playerid")
    val playerId: Long,
    @SerializedName("playername")
    val playerName: String,
    @SerializedName("routines")
    val analysis: List<AnalysisDTO>
) : Serializable

data class PlayerDTO(
    val id: Long,
    @SerializedName("playername")
    val playerName: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("gender")
    val gender: GenderDTO? = null,
    @SerializedName("ability")
    val ability: AbilityDTO? = null,
    @SerializedName("age_group")
    val ageGroup: KeyValueDTO? = null,
    @SerializedName("weight_range")
    val weightRange: KeyValueDTO? = null,
    @SerializedName("height_range")
    val heightRange: KeyValueDTO? = null,
    @SerializedName("udf_1")
    val udf1: String? = null,
    @SerializedName("udf_2")
    val udf2: String? = null,
) : Serializable

data class PlayerCreateModel(
    @SerializedName("player_name")
    val playerName: String? = null,
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("ability")
    val ability: UserLevel? = null,
    @SerializedName("age_group")
    val ageGroup: Int? = null,
    @SerializedName("weight_range")
    val weightRange: Int? = null,
    @SerializedName("height_range")
    val heightRange: Int? = null,
    @SerializedName("udf_1")
    val udf1: String? = null,
    @SerializedName("udf_2")
    val udf2: String? = null,
) : Serializable

data class PlayerUpdateModel(
    @SerializedName("player_id")
    val playerId: Long,
    @SerializedName("player_name")
    val playerName: String? = null,
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("ability")
    val ability: UserLevel? = null,
    @SerializedName("age_group")
    val ageGroup: Int? = null,
    @SerializedName("weight_range")
    val weightRange: Int? = null,
    @SerializedName("height_range")
    val heightRange: Int? = null,
    @SerializedName("udf_1")
    val udf1: String? = null,
    @SerializedName("udf_2")
    val udf2: String? = null,
) : Serializable


data class KeyValueDTO(
    @SerializedName("key")
    val key: Int,
    @SerializedName("name")
    val name: String,
) : Serializable

data class GenderDTO(
    @SerializedName("key")
    val key: String,
    @SerializedName("gender")
    val name: String,
) : Serializable

data class AbilityDTO(
    @SerializedName("key")
    val key: String,
    @SerializedName("ability")
    val ability: String,
) : Serializable
