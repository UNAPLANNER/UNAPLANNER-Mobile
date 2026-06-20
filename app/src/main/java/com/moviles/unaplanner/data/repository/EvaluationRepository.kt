package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.EvaluationApiService
import com.moviles.unaplanner.data.remote.model.CreateEvaluationRequest
import com.moviles.unaplanner.data.remote.model.EvaluationDto
import com.moviles.unaplanner.data.remote.model.UpdateEvaluationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvaluationRepository @Inject constructor(
    private val apiService: EvaluationApiService
) {
    suspend fun getEvaluations(studentId: Int, courseId: Int): ApiResult<List<EvaluationDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEvaluations(studentId, courseId)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun createEvaluation(studentId: Int, courseId: Int, request: CreateEvaluationRequest): ApiResult<EvaluationDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createEvaluation(studentId, courseId, request)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error creating evaluation")
        }
    }

    suspend fun updateEvaluation(studentId: Int, courseId: Int, evaluationId: Int, request: UpdateEvaluationRequest): ApiResult<EvaluationDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateEvaluation(studentId, courseId, evaluationId, request)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error updating evaluation")
        }
    }

    suspend fun deleteEvaluation(studentId: Int, courseId: Int, evaluationId: Int): ApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deleteEvaluation(studentId, courseId, evaluationId)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error deleting evaluation")
        }
    }
}
