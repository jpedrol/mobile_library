package com.example.library.data.supabase

import com.example.library.AprovacaoEmprestimo
import com.example.library.Convidado
import com.example.library.Evento
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApi {

    // LOGIN = SELECT com filtros
    @GET("usuarios")
    suspend fun login(
        @Query("select")
        select: String = "id,nome_completo,email,matricula,total_lidos,tipo_usuario",

        @Query("matricula")
        matriculaFilter: String,       // eq.2025001

        @Query("senha_hash")
        senhaHashFilter: String,       // eq.algumaCoisa

        @Query("limit")
        limit: Int = 1,

        @Header("apikey")
        apiKey: String,

        @Header("Authorization")
        bearer: String
    ): Response<List<Usuario>>

    // REGISTRO = INSERT
    @Headers("Prefer: return=representation")
    @POST("usuarios")
    suspend fun registrar(
        @Body novoUsuario: UsuarioInsert,

        @Header("apikey")
        apiKey: String,

        @Header("Authorization")
        bearer: String
    ): Response<List<Usuario>>

    @Headers("Prefer: return=representation")
    @POST("eventos")
    suspend fun registrarEvento(
        @Body novoEvento: EventoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>


    // Inserir convidado
    @Headers("Prefer: return=representation")
    @POST("convidados")
    suspend fun registrarConvidado(
        @Body novoConvidado: ConvidadoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    // SupabaseApi.kt
    @GET("eventos")
    suspend fun listarEventos(
        @Query("data_hora") dataInicio: String,
        @Query("data_hora") dataFim: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @GET("rest/v1/emprestimos?status=eq.Pendente&select=*,usuarios(*),livros(*)")
    fun listarAprovacoes(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Call<List<AprovacaoEmprestimo>>
}
