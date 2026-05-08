package com.moviles.unaplanner.navigation

object AppDestinations {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main?initialIndex={initialIndex}"
    fun createMainRoute(index: Int) = "main?initialIndex=$index"
    
    // Sub-destinations for BottomNav
    const val INICIO = "inicio"
    const val CALENDAR = "calendar"
    const val MALLA = "malla"
    const val NOTES = "notes"
    const val CONTACTS = "contacts"

    // Admin
    const val ADMIN_MAIN = "admin_main"
    const val ADMIN_PROFILE = "admin_profile"
    const val CONTACT_DETAIL = "contact_detail/{contactId}"
    fun createContactDetailRoute(contactId: Int) = "contact_detail/$contactId"
    const val NOTE_EDIT = "note_edit/{noteId}"

    fun createNoteEditRoute(noteId: Int?) = if (noteId != null) "note_edit/$noteId" else "note_edit/new"
}
