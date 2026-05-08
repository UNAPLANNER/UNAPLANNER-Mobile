package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch

data class ContactAdminUiState(
    val contacts: List<CampusContact> = emptyList(),
    val filteredContacts: List<CampusContact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ContactAdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ContactAdminUiState())
        private set

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            
            var currentUser = AuthSession.currentUser
            Log.d("ContactAdminVM", "Usuario en sesión: $currentUser")
            
            var campusId = currentUser?.campusId
            val userId = currentUser?.id

            // Si el campusId es nulo, intentamos obtenerlo del perfil completo
            if (campusId == null && userId != null) {
                Log.d("ContactAdminVM", "campusId es nulo, intentando obtener perfil para userId: $userId")
                when (val profileResult = repository.getProfile(userId)) {
                    is ApiResult.Success -> {
                        campusId = profileResult.data.campusId
                        Log.d("ContactAdminVM", "Perfil obtenido con éxito. Nuevo campusId: $campusId")
                        AuthSession.currentUser?.let { current ->
                            AuthSession.setUser(profileResult.data.copy(token = current.token))
                        }
                    }
                    is ApiResult.Error -> {
                        // Solo logueamos el error, pero no cortamos el flujo para que entre el fallback
                        Log.e("ContactAdminVM", "Error al obtener perfil: ${profileResult.message}")
                    }
                }
            }

            if (campusId == null) {
                Log.w("ContactAdminVM", "campusId sigue siendo nulo. Usando fallback forzado: 1")
                campusId = 1 // FORZAMOS ID 1 PARA QUE LA LISTA CARGUE
            }

            Log.d("ContactAdminVM", "Llamando a getContactsByCampus con campusId: $campusId")
            when (val result = repository.getContactsByCampus(campusId)) {
                is ApiResult.Success -> {
                    Log.d("ContactAdminVM", "Contactos obtenidos: ${result.data.size}")
                    uiState = uiState.copy(
                        contacts = result.data,
                        filteredContacts = result.data,
                        isLoading = false
                    )
                }
                is ApiResult.Error -> {
                    Log.e("ContactAdminVM", "Error al obtener contactos: ${result.message}")
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
        filterContacts()
    }

    private fun filterContacts() {
        val query = uiState.searchQuery.lowercase().trim()
        uiState = if (query.isEmpty()) {
            uiState.copy(filteredContacts = uiState.contacts)
        } else {
            val filtered = uiState.contacts.filter {
                (it.departmentName?.lowercase()?.contains(query) ?: false) ||
                (it.description?.lowercase()?.contains(query) ?: false) ||
                (it.email?.lowercase()?.contains(query) ?: false) ||
                (it.phone?.contains(query) ?: false)
            }
            uiState.copy(filteredContacts = filtered)
        }
    }

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContactAdminViewModel() as T
        }
    }
}
