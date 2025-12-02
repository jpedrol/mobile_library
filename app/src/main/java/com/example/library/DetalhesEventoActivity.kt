package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var layoutConvidados: LinearLayout

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
        layoutConvidados = findViewById(R.id.container_secao_convidados)

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

        botaoVoltar.setOnClickListener {
            finish()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, FormularioEventosActivity::class.java).apply {
                putExtra("EVENTO_ID", eventoId)
                putExtra("EVENTO_NOME", nomeEvento)
                putExtra("EVENTO_TIPO", tipoEvento)
                putExtra("EVENTO_LOCAL", localEvento)
                putExtra("EVENTO_DATA_HORA", dataHoraString)
                putExtra("EVENTO_IMAGEM_URL", imagemUrl)
            }
            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            mostrarPopupExclusao()
        }
    }

    private fun buscarEExibirConvidados(idDoEvento: Long) {
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
                    layoutConvidados.removeAllViews()

                    if (convidados.isNotEmpty()) {
                        findViewById<LinearLayout>(R.id.container_secao_convidados).visibility = View.VISIBLE
                        convidados.forEach { convidado ->
                            adicionarViewConvidado(convidado.nome, convidado.descricao)
                        }
                    } else {
                        findViewById<LinearLayout>(R.id.container_secao_convidados).visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@DetalhesEventoActivity, "Erro ao buscar convidados: ${response.code()}", Toast.LENGTH_SHORT).show()
                    findViewById<LinearLayout>(R.id.container_secao_convidados).visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesEventoActivity, "Falha na conexão ao buscar convidados: ${e.message}", Toast.LENGTH_LONG).show()
                findViewById<LinearLayout>(R.id.container_secao_convidados).visibility = View.GONE
            }
        }
    }

    private fun adicionarViewConvidado(nome: String, descricao: String) {
        val nomeView = TextView(this).apply {
            text = nome
            textSize = 13f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt() // 8dp
            }
        }

        val nomeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (12 * resources.displayMetrics.density).toInt() // 12dp
            }
            val iconView = ImageView(context).apply {
                setImageResource(R.drawable.ic_pessoa)
                layoutParams = LinearLayout.LayoutParams(
                    (20 * resources.displayMetrics.density).toInt(),
                    (20 * resources.displayMetrics.density).toInt()
                )
            }
            addView(iconView)
            addView(nomeView)
        }

        val descricaoView = TextView(this).apply {
            text = descricao
            textSize = 12f
            setTextColor(ContextCompat.getColor(context, R.color.black))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (28 * resources.displayMetrics.density).toInt() // 28dp
                bottomMargin = (12 * resources.displayMetrics.density).toInt() // Adiciona espaço após a descrição
            }
        }

        // Adiciona as novas views ao container principal de convidados
        layoutConvidados.addView(nomeLayout)
        layoutConvidados.addView(descricaoView)
    }
    private fun configurarPermissoesAdmin() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        val isAdmin = (role == "admin")

        btnEditar.visibility = if (isAdmin) View.VISIBLE else View.GONE
        btnExcluir.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun mostrarPopupExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir evento")
            .setMessage("Tem certeza? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                deletarEvento()
            }
            .setNegativeButton("Cancelar", null) // Botão de retorno sem ação
            .show()
    }

    private fun deletarEvento() {
        if (eventoId == -1L) {
            Toast.makeText(this, "ID do evento inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.deletarEvento(
                    idFiltro = "eq.$eventoId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    // --- 🔥 LÓGICA CORRIGIDA AQUI 🔥 ---
                    // Exibe um pop-up de sucesso em vez de um Toast
                    mostrarPopupSucesso()
                } else {
                    Toast.makeText(this@DetalhesEventoActivity, "Erro ao excluir: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesEventoActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarPopupSucesso() {
        AlertDialog.Builder(this)
            .setTitle("Sucesso")
            .setMessage("O evento foi excluído com sucesso.")
            .setPositiveButton("Ok") { _, _ ->
                // Ao clicar em "Ok", redireciona para a tela de calendário
                val intent = Intent(this@DetalhesEventoActivity, EventosMensaisActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish() // Fecha a tela de detalhes para não ficar na pilha
            }
            .setCancelable(false) // Impede que o usuário feche o pop-up clicando fora
            .show()
    }
}
