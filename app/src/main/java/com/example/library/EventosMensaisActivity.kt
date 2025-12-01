package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.util.*
import java.util.Collections.emptyList
import kotlin.text.padStart

class EventosMensaisActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var calendarView: CalendarView
    private lateinit var adapter: EventosAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        calendarView = findViewById(R.id.calendarView)

        configurarAcoes()
        configurarPermissoesAdmin()

        // Listener do CalendarView para abrir a nova tela
        calendarView.setOnDateChangeListener { _, ano, mes, dia ->
            val mesFormatado = (mes + 1).toString().padStart(2, '0')
            val diaFormatado = dia.toString().padStart(2, '0')
            val dataSelecionada = "$ano-$mesFormatado-$diaFormatado"

            val intent = Intent(this, EventoDiarioActivity::class.java)
            intent.putExtra("DATA_SELECIONADA", dataSelecionada)
            startActivity(intent)
        }
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
        val isAdmin = SessionManager.isAdmin(this)
        btnAddEvento.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    // Em EventosMensaisActivity.kt

    private fun carregarEventosDoDia(data: String) { // data está no formato "yyyy-MM-dd"
        lifecycleScope.launch {
            try {
                // Cria o intervalo de um dia completo para a query
                val dataInicioQuery = "gte.${data}T00:00:00"
                val dataFimQuery = "lt.${data}T23:59:59"

                val response = SupabaseClient.api.listarEventos(
                    dataInicio = dataInicioQuery, // Filtro de início do dia
                    dataFim = dataFimQuery,       // Filtro de fim do dia
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista: List<Evento> = response.body() ?: emptyList()
                    adapter.atualizarLista(lista) // 'adapter' deve ser definido
                } else {
                    Toast.makeText(
                        this@EventosMensaisActivity,
                        "Erro ao carregar eventos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@EventosMensaisActivity,
                    "Falha na conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}