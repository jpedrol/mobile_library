package com.example.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.util.*

class EventosMensaisActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton // ✅ BOTÃO ADICIONADO AQUI
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerEventosMes: RecyclerView
    private lateinit var adapterMensal: EventosMensaisAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyStateLayout: LinearLayout

    private var mesAtualCarregado = -1
    private var anoAtualCarregado = -1

    private val detalhesEventoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            if (anoAtualCarregado != -1 && mesAtualCarregado != -1) {
                carregarEventosDoMes(anoAtualCarregado, mesAtualCarregado)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento) // ✅ INICIALIZAÇÃO DO BOTÃO
        calendarView = findViewById(R.id.calendarView)
        recyclerEventosMes = findViewById(R.id.recyclerEventosMes)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMensal)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        configurarLista()
        configurarAcoes()
        configurarPermissoesAdmin() // ✅ Adicionada chamada para configurar permissões

        // Listener para quando o usuário clica em um dia no calendário
        calendarView.setOnDateChangeListener { _, ano, mes, dia ->
            val intent = Intent(this, EventoDiarioActivity::class.java)
            val dataSelecionada = "$ano-${(mes + 1).toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}"
            intent.putExtra("DATA_SELECIONADA", dataSelecionada)
            startActivity(intent)
        }

        val hoje = Calendar.getInstance()
        carregarEventosDoMes(hoje.get(Calendar.YEAR), hoje.get(Calendar.MONTH))
    }

    override fun onResume() {
        super.onResume()
        if(anoAtualCarregado != -1 && mesAtualCarregado != -1) {
            carregarEventosDoMes(anoAtualCarregado, mesAtualCarregado)
        }
    }

    private fun configurarLista() {
        // ✅ PASSA A AÇÃO DE CLIQUE PARA O ADAPTER
        adapterMensal = EventosMensaisAdapter(emptyList()) { eventoClicado ->
            val intent = Intent(this, DetalhesEventoActivity::class.java).apply {
                putExtra("EVENTO_ID", eventoClicado.id)
                putExtra("EVENTO_NOME", eventoClicado.nome)
                putExtra("EVENTO_TIPO", eventoClicado.tipo)
                putExtra("EVENTO_LOCAL", eventoClicado.local)
                putExtra("EVENTO_DATA_HORA", eventoClicado.dataHora)
                putExtra("EVENTO_DESCRICAO", eventoClicado.descricao)
                putExtra("EVENTO_IMAGEM_URL", eventoClicado.imagemUrl)
            }
            detalhesEventoResultLauncher.launch(intent)
        }
        recyclerEventosMes.layoutManager = LinearLayoutManager(this)
        recyclerEventosMes.adapter = adapterMensal
        recyclerEventosMes.isNestedScrollingEnabled = false
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener {
            finish()
        }

        // ✅ AÇÃO DE CLIQUE PARA O BOTÃO DE ADICIONAR
        btnAddEvento.setOnClickListener {
            startActivity(Intent(this, FormularioEventosActivity::class.java))
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (anoAtualCarregado != -1 && mesAtualCarregado != -1) {
                carregarEventosDoMes(anoAtualCarregado, mesAtualCarregado)
            } else {
                val hoje = Calendar.getInstance()
                carregarEventosDoMes(hoje.get(Calendar.YEAR), hoje.get(Calendar.MONTH))
            }
        }
    }

    // ✅ NOVA FUNÇÃO PARA CONTROLAR A VISIBILIDADE DO BOTÃO
    private fun configurarPermissoesAdmin() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        val isAdmin = (role == "admin")
        btnAddEvento.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun carregarEventosDoMes(ano: Int, mes: Int) {
        this.anoAtualCarregado = ano
        this.mesAtualCarregado = mes
        swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            try {
                val filtro = SupabaseApi.RpcMesFiltro(ano_filtro = ano, mes_filtro = mes + 1)
                val response = SupabaseClient.api.buscarEventosDoMes(
                    body = filtro,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista: List<Evento> = response.body() ?: emptyList()
                    adapterMensal.atualizarLista(lista)

                    if (lista.isEmpty()) {
                        recyclerEventosMes.visibility = View.GONE
                        emptyStateLayout.visibility = View.VISIBLE
                    } else {
                        recyclerEventosMes.visibility = View.VISIBLE
                        emptyStateLayout.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@EventosMensaisActivity, "Erro ao carregar eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("EventosMensais", "Erro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("EventosMensaisActivity", "Falha na conexão", e)
                Toast.makeText(this@EventosMensaisActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
                recyclerEventosMes.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
