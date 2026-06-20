package com.moviles.unaplanner.data.remote.model

data class CareerDto(
    val id: Int,
    val name: String,
    val code: String,
    val description: String?,
    val totalCredits: Int
)

data class StudentCurriculumResponse(
    val careerName: String,
    val careerId: Int,
    val levels: List<CurriculumLevelDto>
)

data class CurriculumCourseDto(
    val id: Int,
    val code: String,
    val name: String,
    val credits: Int,
    val theoryHours: Int,
    val practiceHours: Int,
    val labHours: Int,
    val isElective: Boolean,
    val electiveType: String? = "Obligatorio",
    val status: String,
    val finalGrade: Double?
)

data class CurriculumSemesterDto(
    val semester: Int,
    val courses: List<CurriculumCourseDto>
)

data class CurriculumLevelDto(
    val level: Int,
    val semesters: List<CurriculumSemesterDto>
)

data class StudentCourseProgressDto(
    val courseId: Int,
    val code: String,
    val name: String,
    val credits: Int,
    val isElective: Boolean,
    val electiveType: String? = "Obligatorio",
    val level: Int,
    val term: Int,
    val status: String,
    val finalGrade: Double?,
    val semester: Int?,
    val year: Int?
)

data class UpdateCourseStatusRequest(
    val status: String,
    val finalGrade: Double? = null,
    val semester: Int? = null,
    val year: Int? = null
)

data class PrerequisiteDto(
    val courseId: Int,
    val code: String?,
    val name: String?,
    val type: String?,
    val isPassed: Boolean
)

data class CourseDetailDto(
    val courseId: Int,
    val code: String,
    val name: String,
    val credits: Int,
    val theoryHours: Int?,
    val practiceHours: Int?,
    val labHours: Int?,
    val level: Int,
    val term: Int,
    val status: String,
    val finalGrade: Double?,
    val professorName: String?,
    val classroom: String?,
    val schedule: String?,
    val syllabusUrl: String?,
    val prerequisites: List<PrerequisiteDto>?
)
