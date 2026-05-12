package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName


data class RegisterRequest(
    @SerializedName("FullName")
    val name: String,

    @SerializedName("Email")
    val email: String,

    @SerializedName("Password")
    val password: String,

    @SerializedName("Campus")
    val campus: String,

    @SerializedName("Major")
    val major: String,

    @SerializedName("SecondMajor")
    val secondMajor: String?,

    @SerializedName("StudyPlanId")
    val studyPlanId: Int,

    @SerializedName("EntryYear")
    val entryYear: Int,
    @SerializedName("CurrentCycle")
    val currentCycle: String,


    @SerializedName("StudentId")
    val id: Int? = 0,

    @SerializedName("Title")
    val title: String? = null,

    @SerializedName("Content")
    val content: String? = null


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