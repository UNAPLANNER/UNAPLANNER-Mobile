package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("careerId")
    val careerId: Int,

    @SerializedName("studyPlanId")
    val studyPlanId: Int,

    @SerializedName("enterYear")
    val enterYear: Int,

    @SerializedName("currentCycle")
    val currentCycle: String
)

data class RegisterResponse(
    @SerializedName("message", alternate = ["Message"])
    val message: String,

    @SerializedName("isSuccess", alternate = ["IsSuccess"])
    val isSuccess: Boolean,

    @SerializedName("data", alternate = ["Data"])
    val data: StudentData? = null
)

data class StudentData(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,
    @SerializedName("email", alternate = ["Email"])
    val email: String
)

data class CampusDto(
    @SerializedName("id", alternate = ["Id", "campusId", "CampusId"])
    val id: Int,
    @SerializedName("name", alternate = ["Name"])
    val name: String
)

data class CampusCareerDto(
    @SerializedName("careerId", alternate = ["CareerId", "id", "Id"])
    val careerId: Int,
    @SerializedName("name", alternate = ["Name"])
    val name: String,
    @SerializedName("studyPlans", alternate = ["StudyPlans"])
    val studyPlans: List<StudyPlan> = emptyList()
)

data class StudyPlan(
    @SerializedName("studyPlanId", alternate = ["StudyPlanId", "id", "Id"])
    val studyPlanId: Int,
    @SerializedName("careerId", alternate = ["CareerId"])
    val careerId: Int = 0,
    @SerializedName("name", alternate = ["Name"])
    val name: String
)