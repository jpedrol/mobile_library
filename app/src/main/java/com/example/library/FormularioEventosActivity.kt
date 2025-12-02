package com.example.library

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.library.data.supabase.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale // Único import de Locale, o correto.
import java.util.UUID
import kotlin.text.format
import kotlin.text.trim

class FormularioEventosActivity : AppCompatActivity() {

    // --- Declaração das Views ---
    private lateinit var etNome: android.widget.EditText
    private lateinit var etLocal: android.widget.EditText
    private lateinit var etDataHora: android.widget.EditText
    private lateinit var spinnerTipo: android.widget.Spinner
    private lateinit var checkboxConvidados: android.widget.CheckBox
    private lateinit var containerConvidados: android.widget.LinearLayout
    private lateinit var btnAdicionarConvidado: Button
    private lateinit var layoutAdicionarImagem: android.widget.LinearLayout
    private lateinit var ivImagemEvento: android.widget.ImageView
    private lateinit var layoutPlaceholder: android.widget.LinearLayout
    private lateinit var btnCriarEvento: Button

    // --- Variáveis de estado ---
    private var imagemUri: Uri? = null
    private val REQUEST_IMAGEM = 100
    private var modoEdicao = false
    private var eventoIdParaEditar: Long = -1
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://iuzlnadvrkhmcmhedejt.supabase.co",
            supabaseKey = SupabaseConfig.SUPABASE_KEY
        ) {
           install(Storage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_evento)

        etNome = findViewById(R.id.etNome)
        etLocal = findViewById(R.id.etLocal)
        etDataHora = findViewById(R.id.etDataHora)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        checkboxConvidados = findViewById(R.id.checkboxConvidados)
        containerConvidados = findViewById(R.id.containerConvidados)
        btnAdicionarConvidado = findViewById(R.id.btnAdicionarConvidado)
        layoutAdicionarImagem = findViewById(R.id.layoutAdicionarImagem)
        ivImagemEvento = findViewById(R.id.ivImagemEvento)
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder)
        btnCriarEvento = findViewById(R.id.btnCriarEvento)
        val btnVoltar: ImageButton = findViewById(R.id.botao_voltar)
        val tvTitulo: TextView = findViewById(R.id.tvTitulo)

        if (intent.hasExtra("EVENTO_ID")) {
            modoEdicao = true
            eventoIdParaEditar = intent.getLongExtra("EVENTO_ID", -1)
            tvTitulo.text = "EDITAR EVENTO"
            btnCriarEvento.text = "Salvar Alterações"
            preencherFormularioParaEdicao()
        }

        val tipos = listOf("Clube do Livro", "Lançamento", "Roda de Conversa")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        etDataHora.setOnClickListener { abrirDateTimePicker() }

        checkboxConvidados.setOnCheckedChangeListener { _, checked ->
            containerConvidados.visibility = if (checked) View.VISIBLE else View.GONE
            btnAdicionarConvidado.visibility = if (checked) View.VISIBLE else View.GONE
        }

        // --- CORREÇÃO: Removido o código errado do btnEditar ---
        btnAdicionarConvidado.setOnClickListener { adicionarConvidado() }
        layoutAdicionarImagem.setOnClickListener { selecionarImagem() }
        btnCriarEvento.setOnClickListener { salvarEvento() }
        btnVoltar.setOnClickListener { finish() }
    }

    private fun preencherFormularioParaEdicao() {
        etNome.setText(intent.getStringExtra("EVENTO_NOME"))
        etLocal.setText(intent.getStringExtra("EVENTO_LOCAL"))

        val dataHoraApi = intent.getStringExtra("EVENTO_DATA_HORA")
        try {
            // CORREÇÃO: Usando o Locale correto (java.util.Locale)
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dataFormatada = parser.parse(dataHoraApi ?: "")?.let { formatter.format(it) } ?: ""
            etDataHora.setText(dataFormatada)
        } catch (e: java.lang.Exception) {
            // Deixa em branco se houver erro
        }

        val tipoEvento = intent.getStringExtra("EVENTO_TIPO")
        val tipos = listOf("Clube do Livro", "Lançamento", "Roda de Conversa")
        val tipoPosition = tipos.indexOf(tipoEvento)
        if (tipoPosition >= 0) {
            spinnerTipo.setSelection(tipoPosition)
        }

        val imagemUrl = intent.getStringExtra("EVENTO_IMAGEM_URL")
        if (!imagemUrl.isNullOrEmpty()) {
            Glide.with(this).load(imagemUrl).into(ivImagemEvento)
            ivImagemEvento.visibility = View.VISIBLE
            layoutPlaceholder.visibility = View.GONE
        }
        // Lógica para carregar convidados existentes em modo de edição pode ser adicionada aqui
    }

    private fun selecionarImagem() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGEM)
    }

    @Deprecated("Este método foi descontinuado")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGEM && resultCode == Activity.RESULT_OK) {
            imagemUri = data?.data
            ivImagemEvento.setImageURI(imagemUri)
            ivImagemEvento.visibility = View.VISIBLE
            layoutPlaceholder.visibility = View.GONE
        }
    }

    private fun abrirDateTimePicker() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(this, { _, ano, mes, dia ->
            TimePickerDialog(this, { _, hora, minuto ->
                etDataHora.setText(String.format("%02d/%02d/%04d %02d:%02d", dia, mes + 1, ano, hora, minuto))
            },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            ).show()
        },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun adicionarConvidado() {
        val novoCard = LayoutInflater.from(this).inflate(R.layout.item_convidado_formulario, containerConvidados, false) as CardView
        val tvNumero = novoCard.findViewById<TextView>(R.id.tvConvidadoNumero)
        tvNumero.text = "Convidado ${containerConvidados.childCount + 1}"
        val btnRemover = novoCard.findViewById<ImageButton>(R.id.btnRemoverConvidado)
        btnRemover.setOnClickListener {
            containerConvidados.removeView(novoCard)
            atualizarNumerosConvidados()
        }
        containerConvidados.addView(novoCard)
    }

    private fun atualizarNumerosConvidados() {
        for (i in 0 until containerConvidados.childCount) {
            val card = containerConvidados.getChildAt(i)
            val tvNumero = card.findViewById<TextView>(R.id.tvConvidadoNumero)
            tvNumero.text = "Convidado ${i + 1}"
        }
    }

    private fun salvarEvento() {
        val nome = etNome.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()
        val local = etLocal.text.toString().trim()
        val dataHoraTexto = etDataHora.text.toString().trim()

        if (nome.isEmpty() || local.isEmpty() || dataHoraTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            return
        }

        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formatoSaida = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        val dataHoraApi: String = try {
            formatoEntrada.parse(dataHoraTexto)?.let { formatoSaida.format(it) } ?: ""
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Formato de data inválido!", Toast.LENGTH_SHORT).show()
            return
        }
        if (dataHoraApi.isEmpty()) return

        btnCriarEvento.isEnabled = false
        if (modoEdicao) {
            atualizarEvento(nome, tipo, local, dataHoraApi)
        } else {
            criarNovoEvento(nome, tipo, local, dataHoraApi)
        }
    }

    private fun criarNovoEvento(nome: String, tipo: String, local: String, dataHoraApi: String) {
        lifecycleScope.launch {
            try {
                var urlImagem: String? = null
                if (imagemUri != null) {
                    val nomeArquivo = "imagens_eventos/${UUID.randomUUID()}.jpg"
                    val bytes = contentResolver.openInputStream(imagemUri!!)?.readBytes()
                    if (bytes != null) {
                        urlImagem = supabase.storage["imagens_eventos"].upload(nomeArquivo, bytes, upsert = false)
                    }
                }

                val eventoInsert = EventoInsert(nome = nome, tipo = tipo, local = local, data_hora = dataHoraApi, imagem_url = urlImagem)
                val responseEvento = SupabaseClient.api.registrarEvento(
                    novoEvento = eventoInsert,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (responseEvento.isSuccessful) {
                    val eventoId = responseEvento.body()?.firstOrNull()?.id
                    if (checkboxConvidados.isChecked && eventoId != null) {
                        salvarConvidados(eventoId)
                    }
                    Toast.makeText(this@FormularioEventosActivity, "Evento criado com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    throw Exception("Erro ao criar evento: ${responseEvento.code()} - ${responseEvento.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormularioEventosActivity, "Falha na operação: ${e.message}", Toast.LENGTH_LONG).show()
                btnCriarEvento.isEnabled = true
            }
        }
    }

    private fun atualizarEvento(nome: String, tipo: String, local: String, dataHoraApi: String) {
        lifecycleScope.launch {
            try {
                var urlImagem: String? = intent.getStringExtra("EVENTO_IMAGEM_URL")
                if (imagemUri != null) {
                    val nomeArquivo = "imagens_eventos/${UUID.randomUUID()}.jpg"
                    val bytes = contentResolver.openInputStream(imagemUri!!)?.readBytes()
                    if (bytes != null) {
                        urlImagem = supabase.storage["imagens_eventos"].upload(nomeArquivo, bytes, upsert = false)
                    }
                }

                val eventoUpdate = EventoUpdate(nome = nome, tipo = tipo, local = local, data_hora = dataHoraApi, imagem_url = urlImagem)
                val eventoId: kotlin.Long = eventoIdParaEditar
                val response = SupabaseClient.api.atualizarEvento(
                    idFiltro = "eq.$eventoIdParaEditar",
                    body = eventoUpdate,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    // Lógica para atualizar convidados pode ser adicionada aqui se necessário
                    Toast.makeText(this@FormularioEventosActivity, "Evento atualizado!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@FormularioEventosActivity, EventosMensaisActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                } else {
                    throw Exception("Erro ao atualizar evento: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormularioEventosActivity, "Falha na operação: ${e.message}", Toast.LENGTH_LONG).show()
                btnCriarEvento.isEnabled = true
            }
        }
    }

    private suspend fun salvarConvidados(eventoId: Long) {
        for (i in 0 until containerConvidados.childCount) {
            val card = containerConvidados.getChildAt(i) as CardView
            val etNomeConv = card.findViewById<EditText>(R.id.etNomeConvidado)
            val etDescConv = card.findViewById<EditText>(R.id.etDescricaoConvidado)
            val nomeConvidado = etNomeConv.text.toString().trim()

            if (nomeConvidado.isNotEmpty()) {
                val convidado = ConvidadoInsert(
                    evento_id = eventoId,
                    nome = nomeConvidado,
                    descricao = etDescConv.text.toString().trim()
                )
                // A chamada à API é suspensa, então a corrotina vai esperar
                SupabaseClient.api.registrarConvidado(
                    novoConvidado = convidado,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )
            }
        }
    }
}
