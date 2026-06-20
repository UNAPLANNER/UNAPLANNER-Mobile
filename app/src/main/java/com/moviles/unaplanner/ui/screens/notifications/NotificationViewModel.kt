package com.moviles.unaplanner.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.NotificationRepository
import com.moviles.unaplanner.data.remote.model.NotificationDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    init {
        // Listen for new notification events in real time
        viewModelScope.launch {
            repository.newNotificationEvents.collect {
                loadNotifications()
            }
        }
    }

    private val _notifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    
    private val _selectedFilter = MutableStateFlow("Todas")
    val selectedFilter: StateFlow<String> = _selectedFilter

    val notifications: StateFlow<List<NotificationDto>> = combine(_notifications, _selectedFilter) { list, filter ->
        when (filter) {
            "Todas" -> list
            "Exámenes" -> list.filter {
                val t = it.type.lowercase()
                t.contains("exam") || t.contains("examen")
            }
            "Tareas" -> list.filter {
                val t = it.type.lowercase()
                t.contains("assignment") || t.contains("tarea")
            }
            "Proyectos" -> list.filter {
                val t = it.type.lowercase()
                t.contains("project") || t.contains("proyecto")
            }
            "Eventos" -> list.filter {
                val t = it.type.lowercase()
                t.contains("event") || t.contains("evento")
            }
            else -> list.filter { it.type.equals(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadNotifications() {
        val userId = AuthSession.currentUser?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getNotifications(userId)) {
                is ApiResult.Success -> {
                    _notifications.value = result.data
                }
                is ApiResult.Error -> { /* Error handled in UI if needed */ }
            }
            _isLoading.value = false
        }
    }

    fun markAsRead(notificationId: Int) {
        val userId = AuthSession.currentUser?.id ?: return
        viewModelScope.launch {
            repository.markAsRead(userId, notificationId)
            // Update local state for immediate feedback
            _notifications.value = _notifications.value.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
        }
    }

    fun markAllAsRead() {
        val userId = AuthSession.currentUser?.id ?: return
        viewModelScope.launch {
            repository.markAllAsRead(userId)
            _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        }
    }

    fun deleteNotification(notificationId: Int) {
        val userId = AuthSession.currentUser?.id ?: return
        // Optimistic update
        val previous = _notifications.value
        _notifications.value = previous.filter { it.id != notificationId }
        viewModelScope.launch {
            val result = repository.deleteNotification(userId, notificationId)
            if (result is ApiResult.Error) {
                // Revert if backend rejected the delete
                _notifications.value = previous
            }
        }
    }

    fun deleteAllNotifications() {
        val userId = AuthSession.currentUser?.id ?: return
        val previous = _notifications.value
        _notifications.value = emptyList()
        viewModelScope.launch {
            val result = repository.deleteAllNotifications(userId)
            if (result is ApiResult.Error) {
                _notifications.value = previous
            }
        }
    }

    fun clearForNewSession() {
        _notifications.value = emptyList()
        _selectedFilter.value = "All"
        _isLoading.value = false
    }
}
