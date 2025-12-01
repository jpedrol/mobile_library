package com.example.library.data.supabase

data class Usuario(
    val id: Long,
    val nome_completo: String,
    val email: String,
    val matricula: String,
    val total_lidos: Int,
    val tipo_usuario: String
)
