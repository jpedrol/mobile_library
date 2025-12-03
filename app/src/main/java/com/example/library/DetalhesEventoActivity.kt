package com.example.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetalhesEventoActivity : AppCompatActivity() {

    private var eventoId: Long = -1L
    private lateinit var botaoVoltar: ImageButton
    private lateinit var btnEditar: Button
    private lateinit var btnExcluir: Button
    // ✅ Vamos usar as duas referências para controlar a visibilidade e o conteúdo
    private lateinit var secaoConvidados: LinearLayout
    private lateinit var containerConvidados: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_evento)

        botaoVoltar = findViewById(R.id.botao_voltar)
        btnEditar = findViewById(R.id.btnEditarEvento)
        btnExcluir = findViewById(R.id.btnExcluirEvento)
        val titulo = findViewById<TextView>(R.id.tvTituloEvento)
        val nomeCard = findViewById<TextView>(R.id.tvNomeEventoCard)
        val tipo = findViewById<TextView>(R.id.tvTipoEvento)
        val dataHora = findViewById<TextView>(R.id.tvDataHora)
        val local = findViewById<TextView>(R.id.tvLocalEvento)
        val imagem = findViewById<ImageView>(R.id.ivImagemEvento)

        secaoConvidados = findViewById(R.id.layout_secao_convidados)
        containerConvidados = findViewById(R.id.container_secao_convidados)

        eventoId = intent.getLongExtra("EVENTO_ID", -1L)
        val nomeEvento = intent.getStringExtra("EVENTO_NOME")
        val tipoEvento = intent.getStringExtra("EVENTO_TIPO")
        val localEvento = intent.getStringExtra("EVENTO_LOCAL")
        val dataHoraString = intent.getStringExtra("EVENTO_DATA_HORA")
        val imagemUrl = intent.getStringExtra("EVENTO_IMAGEM_URL")

        titulo.text = nomeEvento
        nomeCard.text = nomeEvento
        tipo.text = tipoEvento ?: "Não informado"
        local.text = localEvento ?: "Não informado"

        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
            dataHora.text = parser.parse(dataHoraString ?: "")?.let { formatter.format(it) } ?: "Inválida"
        } catch (e: Exception) {
            dataHora.text = "Data inválida"
        }

        Glide.with(this)
            .load(imagemUrl)
            .placeholder(R.drawable.ic_livro_aberto)
            .error(R.drawable.ic_livro_aberto)
            .into(imagem)

        if (eventoId != -1L) {
            buscarEExibirConvidados(eventoId)
        }

        configurarPermissoesAdmin()
        configurarListeners()
    }

    private fun configurarListeners() {
        botaoVoltar.setOnClickListener {
            finish()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, FormularioEventosActivity::class.java).apply {
                // ✅ CORREÇÃO: Usando as variáveis locais que já contêm os dados.
                putExtra("EVENTO_ID", eventoId)
                putExtra("EVENTO_NOME", this@DetalhesEventoActivity.intent.getStringExtra("EVENTO_NOME"))
                putExtra("EVENTO_TIPO", this@DetalhesEventoActivity.intent.getStringExtra("EVENTO_TIPO"))
                putExtra("EVENTO_LOCAL", this@DetalhesEventoActivity.intent.getStringExtra("EVENTO_LOCAL"))
                putExtra("EVENTO_DATA_HORA", this@DetalhesEventoActivity.intent.getStringExtra("EVENTO_DATA_HORA"))
                putExtra("EVENTO_IMAGEM_URL", this@DetalhesEventoActivity.intent.getStringExtra("EVENTO_IMAGEM_URL"))
            }
            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            mostrarPopupConfirmacaoExclusao()
        }
    }


    private fun configurarPermissoesAdmin() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        val isAdmin = (role == "admin")
        btnEditar.visibility = if (isAdmin) View.VISIBLE else View.GONE
        btnExcluir.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun buscarEExibirConvidados(idDoEvento: Long) {
        secaoConvidados.visibility = View.GONE
        containerConvidados.removeAllViews() // Limpa convidados antigos

        lifecycleScope.launch {
            try {
                val filtroId = "eq.$idDoEvento"
                val response = SupabaseClient.api.buscarConvidadosPorEvento(
                    idEventoFiltro = filtroId,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val convidados = response.body() ?: emptyList()

                    if (convidados.isNotEmpty()) {
                        secaoConvidados.visibility = View.VISIBLE
                        convidados.forEach { convidado ->
                            adicionarViewConvidado(convidado.nome, convidado.descricao)
                        }
                    } else {
                        secaoConvidados.visibility = View.GONE
                    }
                } else {
                    Log.e("DetalhesEvento", "Erro ao buscar convidados: ${response.code()} - ${response.errorBody()?.string()}")
                    secaoConvidados.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("DetalhesEvento", "Falha na conexão ao buscar convidados", e)
                secaoConvidados.visibility = View.GONE
            }
        }
    }

    private fun adicionarViewConvidado(nome: String, descricao: String) {
        val inflater = LayoutInflater.from(this)
        val convidadoView = inflater.inflate(R.layout.item_detalhe_convidado, containerConvidados, false)

        val nomeConvidado = convidadoView.findViewById<TextView>(R.id.tvNomeConvidado)
        val descricaoConvidado = convidadoView.findViewById<TextView>(R.id.tvDescricaoConvidado)

        nomeConvidado.text = nome
        descricaoConvidado.text = descricao

        containerConvidados.addView(convidadoView)
    }

    private fun mostrarPopupConfirmacaoExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir evento")
            .setMessage("Tem certeza que deseja excluir este evento? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                deletarEventoDoBanco()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletarEventoDoBanco() {
        if (eventoId == -1L) return
        lifecycleScope.launch {
            try {
                SupabaseClient.api.deletarConvidadosPorEvento(
                    idEventoFiltro = "eq.$eventoId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )
                val response = SupabaseClient.api.deletarEvento(
                    idFiltro = "eq.$eventoId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )
                if (response.isSuccessful) {
                    mostrarPopupSucessoExclusao()
                } else {
                    Toast.makeText(this@DetalhesEventoActivity, "Erro ao excluir: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesEventoActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarPopupSucessoExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Sucesso")
            .setMessage("O evento foi excluído com sucesso.")
            .setPositiveButton("Ok") { _, _ ->
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
