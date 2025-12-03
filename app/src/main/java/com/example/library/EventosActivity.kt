package com.example.library

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EventosActivity : AppCompatActivity() {

    private lateinit var eventRegistrationLauncher: ActivityResultLauncher<Intent>

    private lateinit var calendarView: CalendarView
    private lateinit var rvEventos: RecyclerView
    private lateinit var fabAdicionarEvento: FloatingActionButton
    private lateinit var eventosAdapter: EventosAdapter
    private val supabaseApi = SupabaseClient.api
    private var todosEventos: List<Evento> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        // Inicializa o launcher para receber o resultado da Activity de registro
        eventRegistrationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Evento salvo/excluído com sucesso, recarregar a lista
                carregarTodosEventos()
                Toast.makeText(this, "Lista de eventos atualizada.", Toast.LENGTH_SHORT).show()
            }
        }

        calendarView = findViewById(R.id.calendarView)
        rvEventos = findViewById(R.id.rv_eventos)
        fabAdicionarEvento = findViewById(R.id.fab_adicionar_evento)

        // Configurar RecyclerView
        eventosAdapter = EventosAdapter(emptyList()) { evento ->
            // Listener de clique para edição
            val intent = Intent(this, RegistroEventoActivity::class.java).apply {
                putExtra(RegistroEventoActivity.EXTRA_EVENTO, evento)
            }
            eventRegistrationLauncher.launch(intent)
        }
        rvEventos.layoutManager = LinearLayoutManager(this)
        rvEventos.adapter = eventosAdapter

        // Carregar todos os eventos
        carregarTodosEventos()

        // Listener para a seleção de data no calendário
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            filtrarEventosPorData(selectedDate)
        }

        // Implementar lógica para o FAB (apenas para bibliotecários/administradores)
        if (SessionManager.isAdmin(this)) {
            fabAdicionarEvento.visibility = View.VISIBLE
            fabAdicionarEvento.setOnClickListener {
                val intent = Intent(this, RegistroEventoActivity::class.java)
                eventRegistrationLauncher.launch(intent)
            }
        } else {
            fabAdicionarEvento.visibility = View.GONE
        }
    }

    private fun carregarTodosEventos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.getEventos(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    todosEventos = response.body()!!
                    // Filtra para a data atual por padrão
                    val dataAtual = Calendar.getInstance().time
                    runOnUiThread {
                        filtrarEventosPorData(dataAtual)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@EventosActivity, "Erro ao carregar eventos: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@EventosActivity, "Exceção ao carregar eventos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun filtrarEventosPorData(data: Date) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataFormatada = sdf.format(data)

        val eventosFiltrados = todosEventos.filter { evento ->
            // Assumindo que dataHora está no formato ISO 8601 e precisamos apenas da parte da data
            evento.dataHora.startsWith(dataFormatada)
        }

        eventosAdapter.updateList(eventosFiltrados)
    }
}
