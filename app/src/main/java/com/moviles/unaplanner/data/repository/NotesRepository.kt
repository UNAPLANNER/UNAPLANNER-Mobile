package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.data.remote.model.CreateNoteRequest
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.remote.model.StudentCourseProgressDto
import com.moviles.unaplanner.data.remote.model.UpdateNoteRequest
import java.io.IOException

class NotesRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getStudentNotes(studentId: Int): ApiResult<List<NoteDto>> {
        return try {
            val response = apiService.getStudentNotes(studentId)
            if (response.isSuccessful) {
                val notesResponse = response.body()
                ApiResult.Success(notesResponse?.data ?: emptyList())
            } else if (response.code() == 404) {
                // Para un estudiante nuevo sin notas, el API puede devolver 404 o lista vacía
                ApiResult.Success(emptyList())
            } else {
                ApiResult.Error("Error al obtener notas: ${response.code()}")
            }
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Ocurrió un error inesperado")
        }
    }

    suspend fun createNote(studentId: Int, request: CreateNoteRequest): ApiResult<NoteDto> {
        return try {
            val response = apiService.createNote(studentId, request)
            if (response.isSuccessful) {
                val note = response.body()
                if (note != null) {
                    ApiResult.Success(note)
                } else {
                    ApiResult.Error("Error: respuesta vacía del servidor")
                }
            } else {
                ApiResult.Error("Error al crear la nota: ${response.code()}")
            }
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Ocurrió un error inesperado")
        }
    }

    suspend fun getStudentEnrolledCourses(studentId: Int): ApiResult<List<StudentCourseProgressDto>> {
        return try {
            // We use the endpoint that returns detailed progress (status, etc.)
            val response = apiService.getStudentCurriculumCourses(studentId)
            if (response.isSuccessful) {
                val courses = response.body()
                if (courses != null) {
                    ApiResult.Success(courses)
                } else {
                    ApiResult.Error("No se encontraron cursos")
                }
            } else {
                ApiResult.Error("Error al obtener cursos: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error: ${e.message}")
        }
    }

    suspend fun getNotesByCourse(studentId: Int, courseId: Int): ApiResult<List<NoteDto>> {
        return try {
            val response = apiService.getStudentNotes(studentId, courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body.data)
                else ApiResult.Error("Respuesta vacía del servidor")
            } else {
                ApiResult.Error("Error al obtener notas: ${response.code()}")
            }
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Error inesperado: ${e.message}")
        }
    }

    suspend fun updateNote(noteId: Int, request: UpdateNoteRequest): ApiResult<NoteDto> {
        return try {
            val response = apiService.updateNote(noteId, request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al actualizar la nota: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error: ${e.message}")
        }
    }

    suspend fun deleteNote(noteId: Int): ApiResult<Unit> {
        return try {
            val response = apiService.deleteNote(noteId)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Error al eliminar la nota: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error: ${e.message}")
        }
    }
}
