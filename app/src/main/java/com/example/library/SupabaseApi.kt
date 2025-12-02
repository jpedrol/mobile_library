    package com.example.library.data.supabase

    import retrofit2.Response
    import retrofit2.http.*

    interface SupabaseApi {

        @GET("usuarios")
        suspend fun loginUsuario(
            @Query("matricula") matriculaFilter: String,
            @Query("senha_hash") senhaFilter: String,
            @Query("select") select: String = "id,nome_completo,email,matricula,tipo_usuario",
            @Query("limit") limit: Int = 1,

            @Header("apikey") apiKey: String,
            @Header("Authorization") bearer: String
        ): Response<List<Usuario>>


        @GET("usuarios")
        suspend fun login(
            @Query("select")
            select: String = "id,nome_completo,email,matricula,total_lidos,tipo_usuario",

            @Query("matricula")
            matriculaFilter: String,

            @Query("senha_hash")
            senhaHashFilter: String,

            @Query("limit")
            limit: Int = 1,

            @Header("apikey")
            apiKey: String,

            @Header("Authorization")
            bearer: String
        ): Response<List<Usuario>>

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
