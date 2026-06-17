package com.moviles.unaplanner.data.remote.model


import com.google.gson.annotations.SerializedName
data class StudentProfileDto(
    @SerializedName("studentId", alternate = ["StudentId"])
    val studentId: Int,

    @SerializedName("userId", alternate = ["UserId"])
    val userId: Int = 0,

    @SerializedName("email", alternate = ["Email"])
    val email: String,

    @SerializedName("fullName", alternate = ["FullName"])
    val fullName: String,

    @SerializedName("careerId", alternate = ["CareerId"])
    val careerId: Int,

    @SerializedName("careerName", alternate = ["CareerName"])
    val careerName: String,

    @SerializedName("enterYear", alternate = ["EnterYear"])
    val enterYear: Int?
)

data class UpdateStudentProfileRequest(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("careerId")
    val careerId: Int,

    @SerializedName("enterYear")
    val enterYear: Int
)

data class ProfileResponse(
    @SerializedName("data", alternate = ["Data"])
    val data: StudentProfileDto,

    @SerializedName("message", alternate = ["Message"])
    val message: String = ""
)


