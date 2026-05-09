package com.moviles.unaplanner.core

object UserMessages {
    const val BRAND_NAME = "UNAPLANNER"

    object LoginUi {
        const val HEADER_SUBTITLE = "Reserva de Aulas"
        const val EMAIL_LABEL = "Correo Electronico"
        const val EMAIL_PLACEHOLDER = "usuario@una.ac.cr"
        const val PASSWORD_LABEL = "Contraseña"
        const val BUTTON_LOGIN = "Iniciar sesión"
    }

    object Placeholders {
        const val INICIO = "Pantalla de Inicio (Próximamente)"
        const val CALENDAR = "Pantalla de Calendario (Próximamente)"
        const val MALLA = "Pantalla de Malla Curricular (Próximamente)"
        const val NOTES = "Pantalla de Notas (Próximamente)"
        const val CONTACT = "Pantalla de Contactos (Próximamente)"
    }

    object Auth {
        const val LOGIN_EMPTY_FIELDS = "Por favor, completa todos los campos"
        const val INVALID_CREDENTIALS = "Correo o contraseña incorrectos"
    }

    object CampusContacts {
        const val CREATE_SUCCESS = "¡Contacto creado exitosamente!"
    }
}
