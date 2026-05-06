package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName

data class CampusContact(
    val id: Int,
    val campusId: Int,
    @SerializedName("departamentName")
    val departmentName: String,
    val phone: String?,
    val email: String?,
    val description: String?,
    val isStatus: Boolean,
    val createdDate: String
)
