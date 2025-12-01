package com.example.library

import com.google.gson.annotations.SerializedName

data class Evento(
    // bigint -> Long
    @SerializedName("id")
    val id: Long,

    // nome -> nome (não precisa de anotação, mas é boa prática)
    @SerializedName("nome")
    val nome: String,

    // tipo -> tipo
    @SerializedName("tipo")
    val tipo: String?, // Marque como opcional se puder ser nulo

    // local -> local
    @SerializedName("local")
    val local: String?, // Marque como opcional se puder ser nulo

    // data_hora -> dataHora
    @SerializedName("data_hora")
    val dataHora: String, // Vem como String (timestamp)

    // descricao -> descricao
    @SerializedName("descricao")
    val descricao: String,

    // imagem_url -> imagemUrl
    @SerializedName("imagem_url")
    val imagemUrl: String?,

    // created_at (geralmente não precisamos usar no app, mas podemos mapear)
    @SerializedName("created_at")
    val createdAt: String?
)
