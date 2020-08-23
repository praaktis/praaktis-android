package com.mobile.gympraaktis.domain.entities

import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.data.entities.TimelineEntity
import java.io.Serializable

data class TimelineDTO(
    @SerializedName("challenges")
    val challenges: ArrayList<TimelineChallengeItem>
) : Serializable

fun TimelineDTO.toTimelineEntities(): List<TimelineEntity> {
    val list: MutableList<TimelineEntity> = mutableListOf()
    challenges.forEach { challenge ->
        if (challenge.latest.timePerformed != null && challenge.scores.find { it.attemptId == challenge.latest.attemptId } == null) {
            list.add(challenge.latest.toTimelineEntity(challenge.id, challenge.name))
        }
        list.addAll(
            challenge.scores.map {
                it.toTimelineEntity(challenge.id, challenge.name)
            }
        )
    }
    return list
}