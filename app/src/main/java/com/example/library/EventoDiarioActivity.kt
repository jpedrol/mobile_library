package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.replaceFirstChar
import kotlin.text.titlecase

class EventoDiarioActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var tvTituloPagina: TextView
    private lateinit var containerDiasMes: LinearLayout
    private lateinit var recyclerEventosDia: RecyclerView
    private lateinit var adapter: EventosAdapter

    private var calendario = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_diarios)

        // Recebe a data selecionada da tela anterior
        val dataString = intent.getStringExtra("DATA_SELECIONADA")
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendario.time = dataString?.let { formato.parse(it) } ?: Date()

        // Inicializa as views do layout
        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        tvTituloPagina = findViewById(R.id.tvTituloPagina)
        containerDiasMes = findViewById(R.id.containerDiasMes)
        recyclerEventosDia = findViewById(R.id.recyclerEventosDia)


// Configura o RecyclerView
        recyclerEventosDia.layoutManager = LinearLayoutManager(this)

// 1. INICIALIZE a sua variável de classe 'adapter'
        adapter = EventosAdapter(kotlin.collections.emptyList()) // pode usar emptyList() diretamente

// 2. ATRIBUA a variável já inicializada ao RecyclerView
        recyclerEventosDia.adapter = adapter

        configurarAcoes()
        configurarPermissoesAdmin()

        atualizarUI()
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener {
            // Volta para a tela de calendário (EventosMensaisActivity)
            val intent = Intent(this, EventosMensaisActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        btnAddEvento.setOnClickListener {
            // Vai para o formulário de criação de eventos
            startActivity(Intent(this, FormularioEventosActivity::class.java))
        }
    }

    private fun configurarPermissoesAdmin() {
        val isAdmin = SessionManager.isAdmin(this)
        btnAddEvento.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun atualizarUI() {
        // Atualiza o título da página com o mês e ano
        val formatoTitulo = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
        tvTituloPagina.text = formatoTitulo.format(calendario.time).replaceFirstChar { it.titlecase() }

        // Popula a barra de dias e carrega os eventos
        popularDiasDoMes()
        carregarEventosDoDiaAtual()
    }

    private fun popularDiasDoMes() {
        containerDiasMes.removeAllViews()
        val mesAtual = calendario.clone() as Calendar
        val diasNoMes = mesAtual.getActualMaximum(Calendar.DAY_OF_MONTH)
        val diaSelecionado = calendario.get(Calendar.DAY_OF_YEAR)

        for (i in 1..diasNoMes) {
            mesAtual.set(Calendar.DAY_OF_MONTH, i)
            val diaView = LayoutInflater.from(this).inflate(R.layout.item_dia_do_mes, containerDiasMes, false)
            val tvDia: TextView = diaView.findViewById(R.id.tvDia)
            val tvDiaSemana: TextView = diaView.findViewById(R.id.tvDiaSemana)

            tvDia.text = i.toString()
            tvDiaSemana.text = SimpleDateFormat("E", Locale.getDefault()).format(mesAtual.time).take(3)

            // Destaca o dia selecionado
            if (mesAtual.get(Calendar.DAY_OF_YEAR) == diaSelecionado) {
                diaView.background = ContextCompat.getDrawable(this, R.drawable.bg_dia_selecionado)
                tvDia.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                tvDiaSemana.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }

            diaView.setOnClickListener {
                calendario.set(Calendar.DAY_OF_MONTH, i)
                atualizarUI()
            }
            containerDiasMes.addView(diaView)
        }
    }

    private fun carregarEventosDoDiaAtual() {
        val formatoQuery = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataQuery = formatoQuery.format(calendario.time)

        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.listarEventos(
                    dataInicio = "gte.${dataQuery}T00:00:00",
                    dataFim = "lt.${dataQuery}T23:59:59",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista = response.body() ?: kotlin.collections.emptyList()
                    adapter.atualizarLista(lista)
                } else {
                    Toast.makeText(this@EventoDiarioActivity, "Erro ao carregar eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventoDiarioActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
