package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CreateNoteRequest
import com.moviles.unaplanner.data.remote.model.NoteDto
import java.io.IOException

class NotesRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getStudentNotes(userId: Int): ApiResult<List<NoteDto>> {
        return try {
            val response = apiService.getStudentNotes(userId)
            if (response.isSuccessful) {
                val notesResponse = response.body()
                if (notesResponse != null) {
                    ApiResult.Success(notesResponse.data)
                } else {
                    ApiResult.Error("No se encontraron notas")
                }
            } else {
                ApiResult.Error("Error al obtener notas: ${response.code()}")
            }
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Ocurrió un error inesperado")
        }
    }

    suspend fun createNote(userId: Int, request: CreateNoteRequest): ApiResult<NoteDto> {
        return try {
            val response = apiService.createNote(userId, request)
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
}
