package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName

data class NoteDto(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,
    @SerializedName("title", alternate = ["Title"])
    val title: String,
    @SerializedName("content", alternate = ["Content"])
    val content: String?,
    @SerializedName("courseId", alternate = ["CourseId"])
    val courseId: Int?,
    @SerializedName("createdAt", alternate = ["CreatedAt"])
    val createdAt: String,
    @SerializedName("updatedAt", alternate = ["UpdatedAt"])
    val lastUpdated: String,
    @SerializedName("courseName", alternate = ["CourseName"])
    val courseName: String? = null,
    @SerializedName("course", alternate = ["Course"])
    val course: CourseDto? = null
) {
    val displayCourseName: String
        get() = courseName ?: course?.name ?: "General"
}

data class CourseDto(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,
    @SerializedName("code", alternate = ["Code"])
    val code: String,
    @SerializedName("name", alternate = ["Name"])
    val name: String
)

data class CoursesResponse(
    @SerializedName("data", alternate = ["Data"])
    val data: List<CourseDto>,
    @SerializedName("message", alternate = ["Message"])
    val message: String = ""
)

data class NotesResponse(
    @SerializedName("data", alternate = ["Data"])
    val data: List<NoteDto>
)

data class CreateNoteRequest(
    val title: String,
    val content: String,
    val courseId: Int? = null
)

data class UpdateNoteRequest(
    val title: String,
    val content: String,
    val courseId: Int? = null
)

