package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EventoDiarioActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var tvTituloPagina: TextView
    private lateinit var containerDiasMes: LinearLayout
    private lateinit var recyclerEventosDia: RecyclerView
    private lateinit var adapter: EventosDiariosAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var contentLayout: LinearLayout

    private var calendario = Calendar.getInstance()

    private val detalhesEventoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            carregarEventosDoDiaAtual()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_diarios)

        val dataString = intent.getStringExtra("DATA_SELECIONADA")
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendario.time = dataString?.let { formato.parse(it) } ?: Date()

        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        tvTituloPagina = findViewById(R.id.tvTituloPagina)
        containerDiasMes = findViewById(R.id.containerDiasMes)
        recyclerEventosDia = findViewById(R.id.recyclerEventosDia)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDiario)

        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        contentLayout = findViewById(R.id.contentLayout)

        configurarLista()
        configurarAcoes()
        configurarPermissoesAdmin()
        atualizarUI()
    }

    private fun configurarLista() {
        recyclerEventosDia.layoutManager = LinearLayoutManager(this)
        adapter = EventosDiariosAdapter(emptyList()) { eventoClicado ->
            val intent = Intent(this, DetalhesEventoActivity::class.java)
            intent.putExtra("EVENTO_ID", eventoClicado.id)
            intent.putExtra("EVENTO_NOME", eventoClicado.nome)
            intent.putExtra("EVENTO_TIPO", eventoClicado.tipo)
            intent.putExtra("EVENTO_LOCAL", eventoClicado.local)
            intent.putExtra("EVENTO_DATA_HORA", eventoClicado.dataHora)
            intent.putExtra("EVENTO_DESCRICAO", eventoClicado.descricao)
            intent.putExtra("EVENTO_IMAGEM_URL", eventoClicado.imagemUrl)
            detalhesEventoResultLauncher.launch(intent)
        }
        recyclerEventosDia.adapter = adapter
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener { finish() }
        btnAddEvento.setOnClickListener { startActivity(Intent(this, FormularioEventosActivity::class.java)) }
        swipeRefreshLayout.setOnRefreshListener { carregarEventosDoDiaAtual() }
    }

    private fun configurarPermissoesAdmin() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        btnAddEvento.visibility = if (role == "admin") View.VISIBLE else View.GONE
    }

    private fun atualizarUI() {
        val formatoTitulo = SimpleDateFormat("MMMM, yyyy", Locale("pt", "BR"))
        val tituloFormatado = formatoTitulo.format(calendario.time)
        tvTituloPagina.text = tituloFormatado.replaceFirstChar { it.titlecase(Locale("pt", "BR")) }

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
            tvDiaSemana.text = SimpleDateFormat("E", Locale("pt", "BR")).format(mesAtual.time).take(3)

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
        swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            try {
                val filtro = SupabaseApi.RpcDataFiltro(data_filtro = dataQuery)
                val response = SupabaseClient.api.buscarEventosPorData(
                    body = filtro,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)

                    // ✅ Lógica de visibilidade corrigida
                    if (lista.isEmpty()) {
                        contentLayout.visibility = View.GONE
                        emptyStateLayout.visibility = View.VISIBLE
                    } else {
                        contentLayout.visibility = View.VISIBLE
                        emptyStateLayout.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this@EventoDiarioActivity, "Erro ao carregar eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                    contentLayout.visibility = View.GONE
                    emptyStateLayout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventoDiarioActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
                contentLayout.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
