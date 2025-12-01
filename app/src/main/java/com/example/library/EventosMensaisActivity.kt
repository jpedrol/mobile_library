package com.example.library

import EventosAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class EventosActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var containerDiasMes: LinearLayout
    private lateinit var recyclerEventosDia: RecyclerView
    private lateinit var adapter: EventosAdapter

    private val client = SupabaseConfig.client
    private var dataSelecionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        containerDiasMes = findViewById(R.id.containerDiasMes)
        recyclerEventosDia = findViewById(R.id.recyclerEventosDia)

        recyclerEventosDia.layoutManager = LinearLayoutManager(this)
        adapter = EventosAdapter(emptyList())
        recyclerEventosDia.adapter = adapter

        configurarAcoes()
        carregarDiasDoMes()
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener { finish() }

        btnAddEvento.setOnClickListener {
            startActivity(Intent(this, FormularioEventosActivity::class.java))
        }
    }

    private fun carregarDiasDoMes() {
        val mesAtual = "2025-11"

        for (dia in 1..30) {
            val tv = TextView(this).apply {
                text = dia.toString()
                textSize = 16f
                setPadding(24, 12, 24, 12)

                setOnClickListener {
                    dataSelecionada = "$mesAtual-${dia.toString().padStart(2, '0')}"
                    carregarEventosDoDia(dataSelecionada)
                }
            }

            containerDiasMes.addView(tv)
        }
    }

    private fun carregarEventosDoDia(data: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val lista = client.postgrest["eventos"]
                .select {
                    filter {
                        eq("data", data)
                    }
                }.decodeList<Evento>()

            runOnUiThread {
                adapter.atualizarLista(lista)
            }
        }
    }
}
