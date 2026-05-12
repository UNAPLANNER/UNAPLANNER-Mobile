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
        const val DELETE_SUCCESS = "¡Contacto eliminado exitosamente!"
    }

    object RegisterStudent{
        const val HEADER_TITLE = "Crear cuenta"
        const val HEADER_SUBTITLE = "Completa tus datos para registrarte"
        const val BACK_BUTTON = "< Volver"

        const val NAME_LABEL = "NOMBRE COMPLETO"
        const val NAME_PLACEHOLDER = "Ej: María Juarez Pereira"

        const val EMAIL_LABEL = "CORREO ELECTRÓNICO"
        const val EMAIL_PLACEHOLDER = "usuario@est.una.ac.cr"

        const val PASSWORD_LABEL = "CONTRASEÑA"
        const val PASSWORD_PLACEHOLDER = "Mínimo 8 caracteres"

        const val CONFIRM_PASSWORD_LABEL = "CONFIRMAR CONTRASEÑA"
        const val CONFIRM_PASSWORD_PLACEHOLDER = "Repite tu contraseña"

        const val CAMPUS_LABEL = "CAMPUS"
        const val MAJOR_LABEL = "CARRERA PRINCIPAL"
        const val DOUBLE_MAJOR_LABEL = "¿LLEVÁS DOBLE CARRERA? (OPCIONAL)"
        const val CYCLE_LABEL = "CICLO ACTUAL"

        const val BUTTON_REGISTER = "Registrarme"
    }
    object Errors {
        const val SERVER_ERROR = "El servidor no respondió correctamente."
        const val CONNECTION_ERROR = "No se pudo conectar con el servidor. Revisa tu conexión."
        const val GENERIC_ERROR = "Ocurrió un error inesperado"
        const val AUTH_FAILED = "Credenciales incorrectas o usuario ya existe."
    }
}
