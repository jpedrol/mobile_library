package com.example.library

import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class AprovacaoEmprestimo(
    val id: Long? = null,
    val usuario_id: Long,
    val livro_id: Long,
    val data_solicitacao: String? = null, // vem como string ISO
    val data_inicio: String,
    val data_fim: String,
    val status: String = "Pendente",
    val observacoes: String? = null
)

annotation class Serializable
