package com.example.library.data.supabase

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
}
