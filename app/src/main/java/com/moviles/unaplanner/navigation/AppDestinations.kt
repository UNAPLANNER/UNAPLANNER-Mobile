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
    const val CREATE_CONTACT = "create_contact"
    const val EDIT_CONTACT = "edit_contact/{contactId}"
    fun createContactDetailRoute(contactId: Int) = "contact_detail/$contactId"
    fun createEditContactRoute(contactId: Int) = "edit_contact/$contactId"
    const val NOTE_EDIT = "note_edit/{noteId}?courseId={courseId}"
    const val ADD_ACTIVITY = "add_activity"
    const val EDIT_ACTIVITY = "edit_activity/{eventId}"
    const val PROGRESO = "progreso"
    const val NOTIFICATIONS_LIST = "notifications_list"

    fun createNoteEditRoute(noteId: Int?) = if (noteId != null) "note_edit/$noteId" else "note_edit/new"
    fun createNoteWithCourseRoute(courseId: Int) = "note_edit/new?courseId=$courseId"
    fun createEditActivityRoute(eventId: Int) = "edit_activity/$eventId"

    const val COURSE_DETAIL = "course_detail/{courseId}"
    fun createCourseDetailRoute(courseId: Int) = "course_detail/$courseId"
}
