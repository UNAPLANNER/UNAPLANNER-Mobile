package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.data.remote.model.CreateEvaluationRequest
import com.moviles.unaplanner.data.remote.model.EvaluationDto
import com.moviles.unaplanner.data.remote.model.UpdateEvaluationRequest
import retrofit2.http.*

interface EvaluationApiService {
    @GET("api/student/{studentId}/courses/{courseId}/evaluations")
    suspend fun getEvaluations(
        @Path("studentId") studentId: Int,
        @Path("courseId") courseId: Int
    ): List<EvaluationDto>

    @POST("api/student/{studentId}/courses/{courseId}/evaluations")
    suspend fun createEvaluation(
        @Path("studentId") studentId: Int,
        @Path("courseId") courseId: Int,
        @Body request: CreateEvaluationRequest
    ): EvaluationDto

    @PUT("api/student/{studentId}/courses/{courseId}/evaluations/{id}")
    suspend fun updateEvaluation(
        @Path("studentId") studentId: Int,
        @Path("courseId") courseId: Int,
        @Path("id") evaluationId: Int,
        @Body request: UpdateEvaluationRequest
    ): EvaluationDto

    @DELETE("api/student/{studentId}/courses/{courseId}/evaluations/{id}")
    suspend fun deleteEvaluation(
        @Path("studentId") studentId: Int,
        @Path("courseId") courseId: Int,
        @Path("id") evaluationId: Int
    )
}
