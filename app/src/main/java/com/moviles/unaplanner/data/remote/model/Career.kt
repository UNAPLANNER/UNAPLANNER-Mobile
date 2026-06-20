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

data class StudyPlanDetail(
    val id: Int,
    val name: String,
    val code: String,
    val careerId: Int,
    val careerName: String,
    val careerCode: String,
    val effectiveYear: Int,
    val totalCredits: Int,
    val courseCount: Int,
    val levelCount: Int,
    val cycleCount: Int,
    val levels: List<StudyPlanLevel>
)

data class StudyPlanLevel(
    val level: Int,
    val credits: Int,
    val semesters: List<StudyPlanSemester>
)

data class StudyPlanSemester(
    val semester: Int,
    val courses: List<StudyPlanCourseDetail>
)

data class StudyPlanCourseDetail(
    val id: Int,
    val code: String,
    val name: String,
    val credits: Int,
    val isElective: Boolean,
    val electiveType: String? = "Obligatorio",
    val prerequisites: List<StudyPlanCourseRequirement> = emptyList()
)

data class StudyPlanCourseRequirement(
    val courseId: Int,
    val code: String,
    val name: String,
    val requirementType: String
    val code: String,
    val description: String?,
    val totalCredits: Int,
    val isStatus: Boolean = true
)
