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
        const val TITLE = "Contactos"
        const val SUBTITLE = "Directorio de la Sede"
        const val CREATE_TITLE = "Nuevo Contacto"
        const val CREATE_SUBTITLE = "Agregar oficina al directorio"
        const val EDIT_TITLE = "Editar Contacto"
        const val EDIT_SUBTITLE = "Actualizar información del departamento"
        const val SEARCH_PLACEHOLDER = "Buscar por nombre, depa o teléfono..."
        const val DIRECTORY_LABEL = "DIRECTORIO"
        const val FORM_SECTION_TITLE = "Información del Departamento"
        
        const val CREATE_SUCCESS = "¡Contacto creado exitosamente!"
        const val UPDATE_SUCCESS = "¡Contacto actualizado exitosamente!"
        const val DELETE_SUCCESS = "¡Contacto eliminado exitosamente!"
        const val BTN_SAVE = "Guardar Contacto"
        const val BTN_UPDATE = "Actualizar Contacto"
        
        object Labels {
            const val DEPT_NAME = "Nombre del Departamento"
            const val PHONE = "Teléfono"
            const val EMAIL = "Correo Electrónico (Opcional)"
            const val DESCRIPTION = "Descripción (Opcional)"
        }
        
        object Placeholders {
            const val DEPT_NAME = "Ej: Registro Académico"
            const val PHONE = "Ej: 2766-6001"
            const val EMAIL = "Ej: registro@una.ac.cr"
            const val DESCRIPTION = "Breve descripción de servicios..."
        }
        
        object Errors {
            const val DEPT_NAME_REQUIRED = "El nombre del departamento es obligatorio"
            const val PHONE_REQUIRED = "El teléfono es obligatorio"
            const val MAX_CHARS_100 = "Máximo 100 caracteres"
            const val MAX_CHARS_500 = "Máximo 500 caracteres"
            const val MAX_CHARS_25 = "Máximo 25 caracteres"
            const val MIN_CHARS_7 = "Mínimo 7 caracteres"
            const val INVALID_EMAIL = "Formato de correo inválido"
            const val GENERAL_ERROR = "Error al procesar la solicitud. Intente nuevamente."
            const val CHECK_FIELDS = "Por favor, revise los errores en los campos marcados"
        }
        
        object DeleteDialog {
            const val TITLE = "Eliminar contacto"
            const val MESSAGE = "¿Estás seguro de que deseas eliminar este contacto? Esta acción no se puede deshacer."
            const val CONFIRM = "ELIMINAR"
            const val CANCEL = "CANCELAR"
        }
        
        object States {
            const val EMPTY_TITLE = "No hay contactos registrados"
            const val EMPTY_SUBTITLE = "Los contactos de tu sede aparecerán aquí."
            const val NO_RESULTS_TITLE = "No se encontraron resultados"
            const val NO_RESULTS_SUBTITLE = "Prueba con otros términos de búsqueda."
            const val RETRY = "Reintentar"
            const val NO_NAME = "Sin nombre"
            const val NO_INFO = "Información no disponible"
            const val NO_CONTACTS_FOUND = "No se encontraron contactos"
        }
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
        const val REGISTER_SUCCESS = "¡Registro exitoso!"
        const val NO_OPTIONS = "No hay opciones disponibles"
        const val STUDY_PLAN_REQUIRED = "Debe seleccionar un plan de estudio."
        const val INVALID_ENTRY_YEAR = "Año de ingreso inválido."
    }
    object Errors {
        const val SERVER_ERROR = "El servidor no respondió correctamente."
        const val CONNECTION_ERROR = "No se pudo conectar con el servidor. Revisa tu conexión."
        const val GENERIC_ERROR = "Ocurrió un error inesperado"
        const val AUTH_FAILED = "Credenciales incorrectas o usuario ya existe."
        const val PASSWORDS_DO_NOT_MATCH = "Las contraseñas no coinciden."
    }
}
