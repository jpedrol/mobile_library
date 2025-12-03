package com.example.library

import com.google.gson.annotations.SerializedName

data class Evento(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("tipo")
    val tipo: String?,

    @SerializedName("local")
    val local: String?,
  
    @SerializedName("data_hora")
    val dataHora: String,
  
    @SerializedName("descricao")
    val descricao: String,

    @SerializedName("imagem_url")
    val imagemUrl: String?,

    @SerializedName("created_at")
    val createdAt: String?
)
