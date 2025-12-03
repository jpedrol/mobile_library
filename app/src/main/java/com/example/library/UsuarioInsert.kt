package com.example.library.data.supabase

data class UsuarioInsert(
    val nome_completo: String,
    val email: String,
    val matricula: String,
    val senha_hash: String
)
