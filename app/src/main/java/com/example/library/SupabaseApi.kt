package com.example.library

import retrofit2.Response
import retrofit2.http.*
import com.example.library.data.supabase.UsuarioInsert

interface SupabaseApi {

    // ================= USUÁRIOS =================

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
        @Header("apikey") apiKey: String,
        @Header("Authorization")
        bearer: String
    ): Response<List<Usuario>>

    @Headers("Prefer: return=representation")
    @POST("usuarios")
    suspend fun registrar(
        @Body novoUsuario: UsuarioInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Usuario>>

    // ================= LIVROS =================

    @GET("livros")
    suspend fun getLivros(
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 100,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Livro>>

    @GET("livros")
    suspend fun searchLivros(
        @Query("or") termoBusca: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 100,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Livro>>

    @DELETE("livros")
    suspend fun deletarLivro(
        @Query("id") idFilter: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @Headers("Prefer: return=representation")
    @POST("livros")
    suspend fun registrarLivro(
        @Body novoLivro: Livro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Livro>>

    @GET("livros")
    suspend fun getLivroPorId(
        @Query("id") idFilter: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Livro>>

    @Headers("Prefer: return=representation")
    @PATCH("livros")
    suspend fun atualizarLivro(
        @Query("id") idFilter: String,
        @Body livro: Livro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Livro>>

    // ================= EXEMPLARES =================

    @GET("exemplares")
    suspend fun getExemplaresPorLivro(
        @Query("livro_id") livroIdFilter: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Exemplar>>

    @Headers("Prefer: return=representation")
    @POST("exemplares")
    suspend fun registrarExemplar(
        @Body exemplar: Exemplar,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Exemplar>>

    @Headers("Prefer: return=representation")
    @PATCH("exemplares")
    suspend fun atualizarExemplar(
        @Query("id") idFilter: String,
        @Body exemplar: Exemplar,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Exemplar>>

    @DELETE("exemplares")
    suspend fun deletarExemplar(
        @Query("id") idFilter: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<Unit>

    // ================= AVALIAÇÕES =================

    @GET("avaliacoes")
    suspend fun getAvaliacoesPorLivro(
        @Query("livro_id") livroIdFilter: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Avaliacao>>

    @Headers("Prefer: return=representation")
    @POST("avaliacoes")
    suspend fun registrarAvaliacao(
        @Body novaAvaliacao: Avaliacao,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Avaliacao>>

    @GET("avaliacoes")
    suspend fun getAvaliacoesPendentes(
        @Query("status") statusFilter: String = "eq.pendente",
        @Query("select") select: String = "*,usuario:usuario_id(*),livro:livro_id(*)",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Avaliacao>>

    @Headers("Prefer: return=representation")
    @PATCH("avaliacoes")
    suspend fun atualizarStatusAvaliacao(
        @Query("id") idFilter: String,
        @Body statusUpdate: Map<String, String>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Avaliacao>>

    // ================= RESERVAS / APROVAÇÃO EMPRÉSTIMO =================

    @Headers("Prefer: return=representation")
    @POST("aprovacao_emprestimos")
    suspend fun solicitarEmprestimo(
        @Body solicitacao: AprovacaoEmprestimo,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<AprovacaoEmprestimo>>

    @GET("aprovacao_emprestimos")
    suspend fun getSolicitacoesPendentes(
        @Query("status") statusFilter: String = "eq.pendente",
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<AprovacaoEmprestimo>>

    @Headers("Prefer: return=representation")
    @PATCH("aprovacao_emprestimos")
    suspend fun atualizarStatusSolicitacao(
        @Query("id") idFilter: String,
        @Body statusUpdate: Map<String, String>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<AprovacaoEmprestimo>>

    // ================= EVENTOS =================

    @Headers("Prefer: return=representation")
    @POST("eventos")
    suspend fun registrarEvento(
        @Body novoEvento: EventoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @Headers("Prefer: return=representation")
    @POST("convidados_eventos")
    suspend fun registrarConvidado(
        @Body novoConvidado: ConvidadoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    @POST("rpc/buscar_eventos_do_mes")
    suspend fun buscarEventosDoMes(
        @Body body: RpcMesFiltro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @POST("rpc/buscar_eventos_por_data")
    suspend fun buscarEventosPorData(
        @Body body: RpcDataFiltro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @DELETE("eventos")
    suspend fun deletarEvento(
        @Query("id") idFiltro: String,
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

    @GET("convidados_eventos")
    suspend fun buscarConvidadosPorEvento(
        @Query("evento_id") idEventoFiltro: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    @DELETE("convidados_eventos")
    suspend fun deletarConvidadosPorEvento(
        @Query("evento_id") idEventoFiltro: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<Unit>