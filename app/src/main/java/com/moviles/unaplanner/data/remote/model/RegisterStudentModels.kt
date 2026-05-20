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
    val enterYear: Int
)

data class RegisterResponse(
    @SerializedName("Message")
    val message: String,

    @SerializedName("IsSuccess")
    val isSuccess: Boolean,

    @SerializedName("Data")
    val data: StudentData? = null
)

data class StudentData(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Email")
    val email: String
)

data class StudyPlan(
    val studyPlanId: Int,
    val careerId: Int,
    val name: String
)