package com.example.library

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Exemplar(

    val id: Long? = null,


    @SerializedName("livro_id")
    val livroId: Int,

    val registro: String,
    val isbn: String,
    val editora: String,
    val edicao: String,


    @SerializedName("ano")
    val ano: String,

    val suporte: String,
    val localizacao: String,
    val situacao: String,
    val sinopse: String?
) : Serializable
