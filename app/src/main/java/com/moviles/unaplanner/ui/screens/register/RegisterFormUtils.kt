package com.moviles.unaplanner.ui.screens.register

import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.INVALID_ENTRY_YEAR
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.STUDY_PLAN_REQUIRED
import com.moviles.unaplanner.data.remote.model.StudyPlan

object RegisterFormUtils {

    // ── Validaciones del formulario ──────────────────────────────
    fun validate(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        campus: String,
        selectedMajorId: Int,
        selectedPlan: StudyPlan?,
        entryYear: String
    ): String? {
        if (name.isBlank())
            return "El nombre completo es obligatorio"

        if (email.isBlank())
            return "El correo electrónico es obligatorio"

        if (!isValidUnaEmail(email))
            return "El correo debe tener el formato: nombre.apellido1.apellido2@est.una.ac.cr"

        if (password.isBlank())
            return "La contraseña es obligatoria"

        if (password != confirmPassword)
            return UserMessages.Errors.PASSWORDS_DO_NOT_MATCH

        if (password.length < 8)
            return "La contraseña debe tener al menos 8 caracteres"

        if (campus == "Seleccione un Campus")
            return "Debes seleccionar un campus"

        if (selectedMajorId == 0)
            return "Debes seleccionar una carrera principal"

        if (selectedPlan == null)
            return STUDY_PLAN_REQUIRED

        if (entryYear.isBlank())
            return "El año de ingreso es obligatorio"

        if (entryYear.toIntOrNull() == null)
            return "El año de ingreso debe ser un número válido (Ej: 2024)"

        val year = entryYear.toInt()
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        if (year < 1990 || year > currentYear)
            return "El año de ingreso debe estar entre 1990 y $currentYear"

        return null
    }

    // ── Validación de email institucional UNA ────────────────────
    private val unaEmailRegex = Regex(
        """^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]+(\.[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]+)*@est\.una\.ac\.cr$"""
    )

    fun isValidUnaEmail(email: String): Boolean = unaEmailRegex.matches(email)

    // ── Mapeo de errores del backend ─────────────────────────────
    fun mapError(message: String): String = when {
        message.contains("minimum length of '8'") ||
                message.contains("8 characters") ->
            "La contraseña debe tener al menos 8 caracteres"
        message.contains("already") ||
                message.contains("exist") ||
                message.contains("duplicate") ||
                message.contains("registrado") ->
            "Este correo ya está registrado"
        message.contains("est.una.ac.cr") ||
                message.contains("format") ||
                message.contains("formato") ->
            "El correo debe tener el formato: nombre.apellido1.apellido2@est.una.ac.cr"
        message.contains("email", ignoreCase = true) ->
            "El formato del correo no es válido"
        message.contains("careerId", ignoreCase = true) ||
                message.contains("career", ignoreCase = true) ->
            "Debes seleccionar una carrera válida"
        message.contains("studyPlanId", ignoreCase = true) ||
                message.contains("plan", ignoreCase = true) ->
            "Debes seleccionar un plan de estudios válido"
        message.contains("Unable to resolve host") ||
                message.contains("timeout") ->
            "No se pudo conectar. Revisa tu conexión a internet"
        message.contains("500") ->
            "Error del servidor. Intenta de nuevo más tarde"
        message.isNotBlank() -> message
        else -> "Error inesperado. Intenta de nuevo"
    }
}