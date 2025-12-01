package com.example.library

import kotlinx.serialization.Serializable

@Serializable
data class Evento(
    val id: Int? = null,
    val titulo: String = "",
    val descricao: String = "",
    val data: String = "", // YYYY-MM-DD
    val imagem: String = "" // URL pública do Supabase Storage
)

