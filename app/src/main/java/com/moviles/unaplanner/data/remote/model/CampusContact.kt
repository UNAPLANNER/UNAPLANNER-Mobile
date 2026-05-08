package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName

data class CampusContact(
    val id: Int,
    val campusId: Int,
    @SerializedName("departamentName") // Coincide con Swagger: "departamentName"
    val departmentName: String? = "",
    val phone: String? = null,
    val email: String? = null,
    val description: String? = null,
    val isStatus: Boolean = true,
    val createdDate: String? = null
)
