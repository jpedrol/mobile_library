package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistroEventoActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var etNome: TextInputEditText
    private lateinit var etTipo: TextInputEditText
    private lateinit var etLocal: TextInputEditText
    private lateinit var etDataHora: TextInputEditText
    private lateinit var etDescricao: TextInputEditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private val supabaseApi = SupabaseClient.api
    private var eventoParaEdicao: Evento? = null

    companion object {
        const val EXTRA_EVENTO = "extra_evento"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_evento)

        inicializarViews()
        configurarListeners()

        // Verifica se há um evento para edição
        eventoParaEdicao = intent.getSerializableExtra(EXTRA_EVENTO) as? Evento
        if (eventoParaEdicao != null) {
            tvTitulo.text = "Editar Evento"
            preencherCampos(eventoParaEdicao!!)
            btnExcluir.visibility = View.VISIBLE
        } else {
            tvTitulo.text = "Novo Evento"
            btnExcluir.visibility = View.GONE
        }
    }

    private fun inicializarViews() {
        tvTitulo = findViewById(R.id.tv_titulo_registro_evento)
        etNome = findViewById(R.id.et_nome_evento)
        etTipo = findViewById(R.id.et_tipo_evento)
        etLocal = findViewById(R.id.et_local_evento)
        etDataHora = findViewById(R.id.et_data_hora_evento)
        etDescricao = findViewById(R.id.et_descricao_evento)
        btnSalvar = findViewById(R.id.btn_salvar_evento)
        btnExcluir = findViewById(R.id.btn_excluir_evento)
    }

    private fun configurarListeners() {
        btnSalvar.setOnClickListener {
            salvarEvento()
        }

        btnExcluir.setOnClickListener {
            confirmarExclusao()
        }
    }

    private fun preencherCampos(evento: Evento) {
        etNome.setText(evento.nome)
        etTipo.setText(evento.tipo)
        etLocal.setText(evento.local)
        // A data/hora deve ser exibida no formato que o usuário inseriu ou em um formato legível,
        // mas para fins de edição, manteremos o formato original da API (ISO 8601)
        etDataHora.setText(evento.dataHora)
        etDescricao.setText(evento.descricao)
    }

    private fun validarCampos(): Boolean {
        if (etNome.text.isNullOrBlank() || etTipo.text.isNullOrBlank() ||
            etLocal.text.isNullOrBlank() || etDataHora.text.isNullOrBlank() ||
            etDescricao.text.isNullOrBlank()) {
            Toast.makeText(this, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
            return false
        }
        // Validação básica do formato de data/hora (YYYY-MM-DD HH:MM:SS)
        val dataHoraRegex = Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        if (!etDataHora.text.toString().matches(dataHoraRegex)) {
            Toast.makeText(this, "Formato de Data/Hora inválido. Use YYYY-MM-DD HH:MM:SS.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun salvarEvento() {
        if (!validarCampos()) return

        val nome = etNome.text.toString()
        val tipo = etTipo.text.toString()
        val local = etLocal.text.toString()
        val dataHora = etDataHora.text.toString()
        val descricao = etDescricao.text.toString()

        val evento = Evento(
            id = eventoParaEdicao?.id, // Mantém o ID se for edição
            nome = nome,
            tipo = tipo,
            local = local,
            dataHora = dataHora,
            descricao = descricao
            // createdAt e imagemUrl são opcionais e não são editados aqui
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (eventoParaEdicao == null) {
                    // Novo Evento (POST)
                    supabaseApi.registrarEvento(
                        novoEvento = evento,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                } else {
                    // Edição de Evento (PATCH)
                    supabaseApi.atualizarEvento(
                        idFilter = "eq.${evento.id}",
                        evento = evento,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                }

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegistroEventoActivity, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Indica sucesso para a Activity chamadora
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegistroEventoActivity, "Erro ao salvar evento: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@RegistroEventoActivity, "Exceção ao salvar evento: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmarExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Evento")
            .setMessage("Tem certeza que deseja excluir o evento \"${eventoParaEdicao?.nome}\"?")
            .setPositiveButton("Excluir") { dialog, _ ->
                excluirEvento()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun excluirEvento() {
        val eventoId = eventoParaEdicao?.id ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.deletarEvento(
                    idFilter = "eq.$eventoId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegistroEventoActivity, "Evento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Indica sucesso para a Activity chamadora
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegistroEventoActivity, "Erro ao excluir evento: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@RegistroEventoActivity, "Exceção ao excluir evento: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
