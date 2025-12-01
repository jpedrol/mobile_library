package com.example.library

import com.example.library.data.supabase.Usuario
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

data class AprovacaoEmprestimo(
    val id: Long? = null,
    val usuario_id: Long,
    val livro_id: Long,

    @SerializedName("usuarios")
    val usuario: Usuario,

    @SerializedName("livros")
    val livro: Book,

    val data_solicitacao: String? = null, // vem como string ISO
    val data_inicio: String,
    val data_fim: String,
    val status: String = "Pendente",
    val observacoes: String? = null
)