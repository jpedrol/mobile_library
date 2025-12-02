package com.example.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.data.supabase.SupabaseApi
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.util.*
import java.util.Collections.emptyList

class EventosMensaisActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerEventosMes: RecyclerView
    private lateinit var adapterMensal: EventosMensaisAdapter // O novo adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        calendarView = findViewById(R.id.calendarView)
        recyclerEventosMes = findViewById(R.id.recyclerEventosMes)

        configurarLista()
        configurarAcoes()
        configurarPermissoesAdmin()

        calendarView.setOnDateChangeListener { _, ano, mes, dia ->
            val mesFormatado = (mes + 1).toString().padStart(2, '0')
            val diaFormatado = dia.toString().padStart(2, '0')
            val dataSelecionada = "$ano-$mesFormatado-$diaFormatado"

            carregarEventosDoDia(dataSelecionada)
            val intent = Intent(this, EventoDiarioActivity::class.java)
            intent.putExtra("DATA_SELECIONADA", dataSelecionada)
            startActivity(intent)
        }

        // Carrega os eventos do dia atual ao abrir a tela pela primeira vez
        val hoje = Calendar.getInstance()
        val dataHojeStr = "${hoje.get(Calendar.YEAR)}-${(hoje.get(Calendar.MONTH) + 1)
            .toString().padStart(2, '0')}-${hoje.get(Calendar.DAY_OF_MONTH)
            .toString().padStart(2, '0')}"
        carregarEventosDoDia(dataHojeStr)
    }

    private fun configurarLista() {
        // 1. CRIE o adapter com uma lista vazia. A promessa da 'lateinit' é cumprida aqui.
        adapterMensal = EventosMensaisAdapter(emptyList())

        recyclerEventosMes.layoutManager = LinearLayoutManager(this)
        recyclerEventosMes.adapter = adapterMensal
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener {
            // Volta para o menu inicial
            startActivity(Intent(this, MenuInicialActivity::class.java))
        }

        btnAddEvento.setOnClickListener {
            startActivity(Intent(this, FormularioEventosActivity::class.java))
        }
    }

    private fun configurarPermissoesAdmin() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        val isAdmin = (role == "admin")
        btnAddEvento.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }


    private fun carregarEventosDoDia(data: String) {
        lifecycleScope.launch {
            try {
                val filtro = SupabaseApi.RpcDataFiltro(data_filtro = data)

                    val response = SupabaseClient.api.buscarEventosPorData(
                    body = filtro,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista: List<Evento> = response.body() ?: emptyList()
                    adapterMensal.atualizarLista(lista)
                } else {
                    Toast.makeText(this@EventosMensaisActivity, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("EventosMensais", "Erro na resposta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("EventosMensaisActivity", "Falha ao carregar eventos", e)
                Toast.makeText(this@EventosMensaisActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}