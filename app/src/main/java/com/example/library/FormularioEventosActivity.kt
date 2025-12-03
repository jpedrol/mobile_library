package com.example.library

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.semantics.text
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.library.data.supabase.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FormularioEventosActivity : AppCompatActivity() {

    // --- Views ---
    private lateinit var etNome: EditText
    private lateinit var etLocal: EditText
    private lateinit var etDataHora: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var checkboxConvidados: CheckBox
    private lateinit var containerConvidados: LinearLayout
    private lateinit var btnAdicionarConvidado: Button
    private lateinit var layoutAdicionarImagem: LinearLayout
    private lateinit var ivImagemEvento: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var btnSalvar: Button
    private lateinit var tvTitulo: TextView

    // --- Variáveis de Estado ---
    private var imagemUri: Uri? = null
    private var modoEdicao = false
    private var eventoIdParaEditar: Long = -1L
    private var urlImagemExistente: String? = null

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://iuzlnadvrkhmcmhedejt.supabase.co",
            supabaseKey = SupabaseConfig.SUPABASE_KEY
        ) {
            install(Storage)
        }
    }

    private val seletorDeImagem = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imagemUri = result.data?.data
            ivImagemEvento.setImageURI(imagemUri)
            ivImagemEvento.visibility = View.VISIBLE
            layoutPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_evento)

        inicializarViews()
        configurarSpinner()

        if (intent.hasExtra("EVENTO_ID")) {
            modoEdicao = true
            eventoIdParaEditar = intent.getLongExtra("EVENTO_ID", -1L)
            configurarModoEdicao()
        }

        configurarListeners()
    }

    private fun inicializarViews() {
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
        btnSalvar = findViewById(R.id.btnCriarEvento)
        tvTitulo = findViewById(R.id.tvTitulo)
    }

    private fun configurarSpinner() {
        val tipos = listOf("Clube do Livro", "Lançamento", "Roda de Conversa")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
    }

    private fun configurarModoEdicao() {
        tvTitulo.text = "EDITAR EVENTO"
        btnSalvar.text = "Salvar Alterações"
        preencherFormularioParaEdicao()
    }

    private fun configurarListeners() {
        findViewById<ImageButton>(R.id.botao_voltar).setOnClickListener { finish() }
        etDataHora.setOnClickListener { abrirDateTimePicker() }
        layoutAdicionarImagem.setOnClickListener { selecionarImagem() }
        btnAdicionarConvidado.setOnClickListener { adicionarCardConvidado() }
        btnSalvar.setOnClickListener { validarESalvarEvento() }

        checkboxConvidados.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            containerConvidados.visibility = visibility
            btnAdicionarConvidado.visibility = visibility
        }
    }

    private fun preencherFormularioParaEdicao() {
        etNome.setText(intent.getStringExtra("EVENTO_NOME"))
        etLocal.setText(intent.getStringExtra("EVENTO_LOCAL"))

        val dataHoraApi = intent.getStringExtra("EVENTO_DATA_HORA")
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dataFormatada = dataHoraApi?.let { parser.parse(it)?.let { d -> formatter.format(d) } } ?: ""
            etDataHora.setText(dataFormatada)
        } catch (e: Exception) {
            Log.e("FormularioEventos", "Erro ao formatar data para edição", e)
        }

        val tipoEvento = intent.getStringExtra("EVENTO_TIPO")
        (spinnerTipo.adapter as? ArrayAdapter<String>)?.let { adapter ->
            val position = adapter.getPosition(tipoEvento)
            if (position >= 0) spinnerTipo.setSelection(position)
        }

        urlImagemExistente = intent.getStringExtra("EVENTO_IMAGEM_URL")
        if (!urlImagemExistente.isNullOrEmpty()) {
            ivImagemEvento.visibility = View.VISIBLE
            layoutPlaceholder.visibility = View.GONE
            Glide.with(this).load(urlImagemExistente).into(ivImagemEvento)
        }

        lifecycleScope.launch {
            carregarConvidadosParaEdicao()
        }
    }

    private suspend fun carregarConvidadosParaEdicao() {
        if (eventoIdParaEditar == -1L) return
        try {
            val response = SupabaseClient.api.buscarConvidadosPorEvento(
                idEventoFiltro = "eq.$eventoIdParaEditar",
                apiKey = SupabaseConfig.SUPABASE_KEY,
                bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
            )
            if (response.isSuccessful) {
                val convidados = response.body() ?: emptyList()
                if (convidados.isNotEmpty()) {
                    checkboxConvidados.isChecked = true
                    convidados.forEach { convidado ->
                        adicionarCardConvidado(convidado.nome, convidado.descricao)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FormularioEventos", "Erro ao buscar convidados para edição", e)
        }
    }

    private fun selecionarImagem() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        seletorDeImagem.launch(intent)
    }

    private fun abrirDateTimePicker() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(this, { _, ano, mes, dia ->
            TimePickerDialog(this, { _, hora, minuto ->
                etDataHora.setText(String.format("%02d/%02d/%04d %02d:%02d", dia, mes + 1, ano, hora, minuto))
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show()
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun adicionarCardConvidado(nome: String? = null, descricao: String? = null) {
        val novoCard = LayoutInflater.from(this).inflate(R.layout.item_convidado_formulario, containerConvidados, false) as CardView
        val tvNumero = novoCard.findViewById<TextView>(R.id.tvConvidadoNumero)
        val etNomeConvidado = novoCard.findViewById<EditText>(R.id.etNomeConvidado)
        val etDescricaoConvidado = novoCard.findViewById<EditText>(R.id.etDescricaoConvidado)
        val btnRemover = novoCard.findViewById<ImageButton>(R.id.btnRemoverConvidado)

        tvNumero.text = "Convidado ${containerConvidados.childCount + 1}"
        etNomeConvidado.setText(nome ?: "")
        etDescricaoConvidado.setText(descricao ?: "")

        btnRemover.setOnClickListener {
            containerConvidados.removeView(novoCard)
            atualizarNumerosConvidados()
        }
        containerConvidados.addView(novoCard)
    }

    private fun atualizarNumerosConvidados() {
        for (i in 0 until containerConvidados.childCount) {
            val card = containerConvidados.getChildAt(i)
            card.findViewById<TextView>(R.id.tvConvidadoNumero).text = "Convidado ${i + 1}"
        }
    }

    private fun validarESalvarEvento() {
        val nome = etNome.text.toString().trim()
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
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de data inválido!", Toast.LENGTH_SHORT).show()
            return
        }
        if (dataHoraApi.isEmpty()) return

        btnSalvar.isEnabled = false

        lifecycleScope.launch {
            try {
                var urlImagemFinal = if (modoEdicao) urlImagemExistente else null

                if (imagemUri != null) {
                    val nomeArquivo = "${UUID.randomUUID()}.jpg"
                    val caminhoNoBucket = "imagens_eventos/$nomeArquivo"
                    val bytes = contentResolver.openInputStream(imagemUri!!)?.readBytes()

                    if (bytes != null) {
                        val caminhoRetornado = supabase.storage["imagens_eventos"].upload(caminhoNoBucket, bytes)

                        urlImagemFinal = supabase.storage["imagens_eventos"].publicUrl(caminhoNoBucket)
                        Log.d("FormularioEventos", "Upload concluído. URL gerada: $urlImagemFinal")
                    }
                }

                if (modoEdicao) {
                    val eventoUpdate = EventoUpdate(nome, spinnerTipo.selectedItem.toString(), local, dataHoraApi, urlImagemFinal)
                    atualizarEvento(eventoUpdate)
                } else {
                    val eventoInsert = EventoInsert(nome, spinnerTipo.selectedItem.toString(), local, dataHoraApi, urlImagemFinal)
                    criarNovoEvento(eventoInsert)
                }
            } catch (e: Exception) {
                Log.e("FormularioEventos", "Falha ao salvar evento", e)
                Toast.makeText(this@FormularioEventosActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                btnSalvar.isEnabled = true
            }
        }
    }


    private suspend fun criarNovoEvento(evento: EventoInsert) {
        val response = SupabaseClient.api.registrarEvento(
            novoEvento = evento,
            apiKey = SupabaseConfig.SUPABASE_KEY,
            bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
        )
        if (response.isSuccessful) {
            val novoEventoId = response.body()?.firstOrNull()?.id
            if (novoEventoId != null) {
                salvarConvidados(novoEventoId, modoEdicao = false)
            }
            Toast.makeText(this, "Evento criado com sucesso!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            throw Exception("Erro ao criar evento: ${response.errorBody()?.string()}")
        }
    }

    private suspend fun atualizarEvento(evento: EventoUpdate) {
        val response = SupabaseClient.api.atualizarEvento(
            idFiltro = "eq.$eventoIdParaEditar",
            body = evento,
            apiKey = SupabaseConfig.SUPABASE_KEY,
            bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
        )
        if (response.isSuccessful) {
            salvarConvidados(eventoIdParaEditar, modoEdicao = true)
            Toast.makeText(this, "Evento atualizado com sucesso!", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK) // Informa a tela anterior que houve mudança
            finish()
        } else {
            throw Exception("Erro ao atualizar evento: ${response.errorBody()?.string()}")
        }
    }

    private suspend fun salvarConvidados(eventoId: Long, modoEdicao: Boolean) {
        // Se estiver editando, primeiro apaga todos os convidados existentes para este evento.
        if (modoEdicao) {
            try {
                SupabaseClient.api.deletarConvidadosPorEvento(
                    idEventoFiltro = "eq.$eventoId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )
            } catch (e: Exception) {
                Log.e("FormularioEventos", "Falha ao deletar convidados antigos.", e)
                // Continua mesmo se falhar, para tentar inserir os novos.
            }
        }

        // Se a checkbox estiver marcada, salva a lista atual de convidados do formulário.
        if (checkboxConvidados.isChecked) {
            for (i in 0 until containerConvidados.childCount) {
                val card = containerConvidados.getChildAt(i) as CardView
                val nomeConvidado = card.findViewById<EditText>(R.id.etNomeConvidado).text.toString().trim()
                val descConvidado = card.findViewById<EditText>(R.id.etDescricaoConvidado).text.toString().trim()

                if (nomeConvidado.isNotEmpty()) {
                    val convidadoInsert = ConvidadoInsert(evento_id = eventoId, nome = nomeConvidado, descricao = descConvidado)
                    try {
                        SupabaseClient.api.registrarConvidado(
                            novoConvidado = convidadoInsert,
                            apiKey = SupabaseConfig.SUPABASE_KEY,
                            bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                        )
                    } catch (e: Exception) {
                        Log.e("FormularioEventos", "Falha ao registrar convidado '$nomeConvidado'.", e)
                    }
                }
            }
        }
    }
}
