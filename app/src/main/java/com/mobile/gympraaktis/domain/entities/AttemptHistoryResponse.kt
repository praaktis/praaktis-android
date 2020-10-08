package com.mobile.gympraaktis.domain.entities

data class AttemptHistoryResponse(
    val count: Int,
    val results: List<AttemptDTO>
)