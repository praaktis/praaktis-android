package com.mobile.gympraaktis.domain.model

import com.google.gson.annotations.SerializedName

class RoutinesList : ArrayList<RoutinesList.Routine>() {
    data class Routine(
        @SerializedName("detail_points")
        val detailPoints: List<DetailPoint>,
        @SerializedName("download_date")
        val downloadDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("instructions")
        val instructions: Instructions,
        @SerializedName("name")
        val name: String,
        @SerializedName("video_guide")
        val videoGuide: List<Any>,
        @SerializedName("video_url")
        val videoUrl: String
    ) {
        data class DetailPoint(
            @SerializedName("help_text")
            val helpText: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("label")
            val label: String,
            @SerializedName("max_value")
            val maxValue: Double
        )

        data class Instructions(
            @SerializedName("multiple")
            val multiple: List<String>,
            @SerializedName("single")
            val single: List<String>
        )
    }
}