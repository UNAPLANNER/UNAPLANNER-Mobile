package com.moviles.unaplanner.data.remote.model

data class EvaluationDto(
    val id: Int,
    val name: String,
    val type: String, // e.g., "Examen", "Tarea", "Proyecto"
    val percentage: Double, // e.g., 20.0
    val grade: Double? = null, // Grade obtained (0-100)
    val date: String? = null, // ISO 8601 date string
    val hasReminder: Boolean = false
) {
    val earnedPoints: Double
        get() = ((grade ?: 0.0) / 100.0) * percentage
}

data class CreateEvaluationRequest(
    val name: String,
    val type: String,
    val percentage: Double,
    val date: String? = null,
    val hasReminder: Boolean = false
)

data class UpdateEvaluationRequest(
    val name: String,
    val type: String,
    val percentage: Double,
    val grade: Double? = null,
    val date: String? = null,
    val hasReminder: Boolean = false
)
