package com.moviles.unaplanner.data.remote.model
import com.google.gson.annotations.SerializedName
data class GpaResponseDto(
    @SerializedName("studentId")
    val studentId: Int,

    @SerializedName("gpa")
    val gpa: Int,

    @SerializedName("message")
    val message: String
)

data class GPAStudentProfileDto(
    @SerializedName("studentId")
    val studentId: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("careerId")
    val careerId: Int,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("enterYear")
    val enterYear: Int
)