package com.moviles.unaplanner.data.remote.model

data class AdminDashboard(
    val adminName: String,
    val department: String?,
    val campusId: Int,
    val campusName: String,
    val activeCareers: Int,
    val studyPlans: Int,
    val registeredCourses: Int,
    val activeStudents: Int,
    val latestCareer: AdminDashboardCareer?,
    val careers: List<AdminDashboardCareer>
)

data class AdminDashboardCareer(
    val id: Int,
    val name: String,
    val code: String,
    val totalCredits: Int,
    val courseCount: Int,
    val studyPlanId: Int?,
    val studyPlanYear: Int?,
    val isStatus: Boolean,
    val createdDate: String?
)
