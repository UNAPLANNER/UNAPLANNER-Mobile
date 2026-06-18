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
        if (year < 1990 || year > 2026)
            return "El año de ingreso debe estar entre 1990 y 2026"

        return null
    }

    // ── Mapeo de errores del backend ─────────────────────────────
    fun mapError(message: String): String = when {
        message.contains("minimum length of '8'") ->
            "La contraseña debe tener al menos 8 caracteres"
        message.contains("already") ||
                message.contains("exist") ||
                message.contains("duplicate") ->
            "Este correo ya está registrado"
        message.contains("email") ||
                message.contains("Email") ->
            "El formato del correo no es válido"
        message.contains("careerId") ||
                message.contains("CareerId") ->
            "Debes seleccionar una carrera"
        message.contains("Unable to resolve host") ||
                message.contains("timeout") ->
            "No se pudo conectar. Revisa tu conexión a internet"
        message.contains("500") ->
            "Error del servidor. Intenta de nuevo más tarde"
        else -> "Error inesperado. Intenta de nuevo"
    }
}