package com.example.library

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.net.http.HttpResponseCache.install
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.util.Calendar
import java.util.UUID

class FormularioEventosActivity : AppCompatActivity() {

    private lateinit var supabase: SupabaseClient

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
    private lateinit var btnCriarEvento: Button

    private var imagemUri: Uri? = null
    private val REQUEST_IMAGEM = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_evento)

        // -----------------------------------------
        // Inicializar Supabase
        // -----------------------------------------
        supabase = createSupabaseClient(
            supabaseUrl = "SUPABASE_URL",
            supabaseKey = "SUPABASE_ANON_KEY"
        ) {
            install(io.github.jan.supabase.postgrest.Postgrest)
            install(io.github.jan.supabase.storage.Storage)
        }

        // -----------------------------------------
        // Vincular views
        // -----------------------------------------
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

        // -----------------------------------------
        // Spinner de tipos
        // -----------------------------------------
        val tipos = listOf("Palestra", "Seminário", "Roda de Conversa", "Lançamento", "Outro")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        // -----------------------------------------
        // DateTime Picker
        // -----------------------------------------
        etDataHora.setOnClickListener { abrirDateTimePicker() }

        // -----------------------------------------
        // Checkbox → mostrar campo convidados
        // -----------------------------------------
        checkboxConvidados.setOnCheckedChangeListener { _, checked ->
            containerConvidados.visibility = if (checked) LinearLayout.VISIBLE else LinearLayout.GONE
            btnAdicionarConvidado.visibility = if (checked) Button.VISIBLE else Button.GONE
        }

        // -----------------------------------------
        // Botão ADD convidado
        // -----------------------------------------
        btnAdicionarConvidado.setOnClickListener { adicionarConvidado() }

        // -----------------------------------------
        // Adicionar imagem
        // -----------------------------------------
        layoutAdicionarImagem.setOnClickListener { selecionarImagem() }

        // -----------------------------------------
        // Criar evento
        // -----------------------------------------
        btnCriarEvento.setOnClickListener { salvarEvento() }
    }


    // 🔹 Escolher imagem
    private fun selecionarImagem() {
        val intent = android.content.Intent(android.content.Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGEM)
    }

    // 🔹 Receber resultado imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGEM && resultCode == RESULT_OK) {
            imagemUri = data?.data
            ivImagemEvento.setImageURI(imagemUri)
            ivImagemEvento.visibility = ImageView.VISIBLE
            layoutPlaceholder.visibility = LinearLayout.GONE
        }
    }

    // 🔹 Abrir Date + Time Picker
    private fun abrirDateTimePicker() {
        val calendario = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, ano, mes, dia ->
                TimePickerDialog(
                    this,
                    { _, hora, minuto ->
                        etDataHora.setText(
                            String.format("%02d/%02d/%04d %02d:%02d", dia, mes + 1, ano, hora, minuto)
                        )
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

    // 🔹 Adicionar campo convidado dinamicamente
    private fun adicionarConvidado() {
        val inflater = LayoutInflater.from(this)
        val card = inflater.inflate(R.layout.item_convidado_template, containerConvidados, false)

        val numero = containerConvidados.childCount + 1
        card.findViewById<TextView>(R.id.tvConvidadoNumero).text = "Convidado $numero"

        val btnRemover = card.findViewById<ImageButton>(R.id.btnRemoverConvidado)
        btnRemover.setOnClickListener {
            containerConvidados.removeView(card)
        }

        containerConvidados.addView(card)
    }

    // 🔹 Salvar evento no Supabase
    private fun salvarEvento() {
        val nome = etNome.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()
        val local = etLocal.text.toString().trim()
        val dataHora = etDataHora.text.toString().trim()

        if (nome.isEmpty() || local.isEmpty() || dataHora.isEmpty()) {
            Snackbar.make(btnCriarEvento, "Preencha todos os campos obrigatórios!", Snackbar.LENGTH_LONG).show()
            return
        }

        Thread {
            try {
                // -----------------------------------------
                // UPLOAD DE IMAGEM
                // -----------------------------------------
                var urlImagem: String? = null

                if (imagemUri != null) {
                    val bucket = supabase.storage.from("eventos")

                    val nomeArquivo = "${UUID.randomUUID()}.jpg"
                    val input = contentResolver.openInputStream(imagemUri!!)!!

                    bucket.upload(nomeArquivo, input, upsert = true)

                    val publicUrl = bucket.publicUrl(nomeArquivo)
                    urlImagem = publicUrl
                }

                // -----------------------------------------
                // INSERIR EVENTO NA TABELA
                // -----------------------------------------
                val response = supabase.postgrest["eventos"].insert(
                    mapOf(
                        "nome" to nome,
                        "tipo" to tipo,
                        "local" to local,
                        "data_hora" to dataHora,
                        "imagem" to urlImagem
                    )
                )

                val eventoId = response.body()?.jsonObject?.get("id")?.toString()?.toInt()

                // Convidados
                if (checkboxConvidados.isChecked && eventoId != null) {
                    for (i in 0 until containerConvidados.childCount) {
                        val card = containerConvidados.getChildAt(i)
                        val nomeConv = card.findViewById<EditText>(R.id.etNomeConvidado).text.toString()
                        val descConv = card.findViewById<EditText>(R.id.etDescricaoConvidado).text.toString()

                        supabase.postgrest["convidados"].insert(
                            mapOf(
                                "evento_id" to eventoId,
                                "nome" to nomeConv,
                                "descricao" to descConv
                            )
                        )
                    }
                }

                runOnUiThread {
                    Snackbar.make(btnCriarEvento, "Evento criado com sucesso!", Snackbar.LENGTH_LONG).show()
                    finish()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Snackbar.make(btnCriarEvento, "Erro ao criar evento: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
