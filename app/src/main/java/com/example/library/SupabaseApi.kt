package com.example.library.data.supabase

import com.example.library.AprovacaoEmprestimo
import com.example.library.Convidado
import com.example.library.Evento
import com.example.library.EventoUpdate
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

    @GET("eventos")
    suspend fun listarEventos(
        // CORREÇÃO: Unifique os filtros de data em um único parâmetro.
        // O Retrofit vai montar a URL assim: /eventos?data_hora=gte.2025...,lt.2025...
        @Query("data_hora::text") dataFiltro: String,
        @Query("select") select: String = "*", // Boa prática: especificar colunas
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

data class RpcDataFiltro(val data_filtro: String)

    // ADICIONE A NOVA FUNÇÃO PARA CHAMAR O RPC
    @POST("rpc/buscar_eventos_por_data")
    suspend fun buscarEventosPorData(
        @Body body: RpcDataFiltro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @DELETE("eventos")
    suspend fun deletarEvento(
        @Query("id") idFiltro: String, // ex: "eq.123"
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @Headers("Prefer: return=representation")
    @PATCH("eventos")
    suspend fun atualizarEvento(
        @Query("id") idFiltro: String,
        @Body body: EventoUpdate,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @GET("convidados_eventos") // Nome da sua tabela de convidados
    suspend fun buscarConvidadosPorEvento(
        @Query("evento_id") idEventoFiltro: String, // ex: "eq.123"
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    @GET("rest/v1/emprestimos?status=eq.Pendente&select=*,usuarios(*),livros(*)")
    fun listarAprovacoes(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Call<List<AprovacaoEmprestimo>>
}
