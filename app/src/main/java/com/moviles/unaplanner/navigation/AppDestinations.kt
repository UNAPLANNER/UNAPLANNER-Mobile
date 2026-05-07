package com.moviles.unaplanner.navigation

object AppDestinations {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    
    // Sub-destinations for BottomNav
    const val INICIO = "inicio"
    const val CALENDAR = "calendar"
    const val MALLA = "malla"
    const val NOTES = "notes"
    const val CONTACTS = "contacts"
    const val NOTE_EDIT = "note_edit/{noteId}"

    fun createNoteEditRoute(noteId: Int?) = if (noteId != null) "note_edit/$noteId" else "note_edit/new"
}
