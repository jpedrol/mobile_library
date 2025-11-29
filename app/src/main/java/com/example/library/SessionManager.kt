package com.example.library

import android.content.Context

object SessionManager {

    private const val PREFS = "user_session"
    private const val KEY_NAME = "username"
    private const val KEY_ADMIN = "isAdmin"

    fun saveUser(context: Context, name: String, isAdmin: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, name)
            .putBoolean(KEY_ADMIN, isAdmin)
            .apply()
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val isAdmin = prefs.getBoolean(KEY_ADMIN, false)

        return if (isAdmin) "Administrador"
        else prefs.getString(KEY_NAME, "Usuário") ?: "Usuário"
    }

    fun isAdmin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ADMIN, false)
    }
}
