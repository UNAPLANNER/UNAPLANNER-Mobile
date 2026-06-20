package com.moviles.unaplanner.data.remote.model

data class Career(
    val id: Int,
    val campusId: Int?,
    val campusName: String?,
    val name: String,
    val code: String,
    val description: String?,
    val totalCredits: Int,
    val currentStudyPlanId: Int?,
    val currentStudyPlanName: String?,
    val currentStudyPlanYear: Int?,
    val courseCount: Int?,
    val levelCount: Int?,
    val isStatus: Boolean?,
    val createdDate: String?
)

data class CreateCareerRequest(
    val name: String,
    val degreeLevel: String,
    val planYear: Int,
    val school: String,
    val bachelorCredits: Int?,
    val diplomaCredits: Int?,
    val degreeCredits: Int?,
    val officialResolution: String,
    val isStatus: Boolean = true
)

data class UpdateCareerRequest(
    val name: String,
    val degreeLevel: String,
    val planYear: Int,
    val school: String,
    val bachelorCredits: Int?,
    val diplomaCredits: Int?,
    val degreeCredits: Int?,
    val officialResolution: String,
    val isStatus: Boolean
)
    val code: String,
    val description: String?,
    val totalCredits: Int,
    val isStatus: Boolean = true
)
