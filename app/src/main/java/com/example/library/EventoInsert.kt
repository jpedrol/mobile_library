package com.example.library.data.supabase

import com.google.gson.annotations.SerializedName

data class EventoInsert(
    val nome: String,
    val tipo: String,
    val local: String,
    val data_hora: String,
    @SerializedName("imagem_url")
    val imagem_url: String? = null
)
