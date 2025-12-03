package com.example.library

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.library.databinding.ActivityAddEditExemplarBinding
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AddEditExemplarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditExemplarBinding

    private val supabaseApi = SupabaseClient.api

    private var livroId: Int = -1
    private var exemplarToEdit: Exemplar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditExemplarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        livroId = intent.getIntExtra("LIVRO_ID", -1)
        exemplarToEdit = intent.getSerializableExtra("EXEMPLAR_EDITAR") as? Exemplar

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        if (exemplarToEdit != null) {
            binding.titleTextView.text = getString(R.string.editar_exemplar)
            fillFields(exemplarToEdit!!)
        } else {
            binding.titleTextView.text = getString(R.string.cadastrar_exemplar)
        }
    }

    private fun fillFields(exemplar: Exemplar) {
        binding.registroEditText.setText(exemplar.registro)
        binding.isbnEditText.setText(exemplar.isbn)
        binding.editoraEditText.setText(exemplar.editora)
        binding.edicaoEditText.setText(exemplar.edicao)

        val anoSomente = exemplar.ano.take(4)
        binding.anoEditText.setText(anoSomente)

        binding.suporteEditText.setText(exemplar.suporte)
        binding.localizacaoEditText.setText(exemplar.localizacao)
        binding.situacaoEditText.setText(exemplar.situacao)
        binding.sinopseEditText.setText(exemplar.sinopse)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener { saveExemplar() }
    }

    private fun saveExemplar() {
        val registro = binding.registroEditText.text.toString().trim()
        val isbn = binding.isbnEditText.text.toString().trim()
        val editora = binding.editoraEditText.text.toString().trim()
        val edicao = binding.edicaoEditText.text.toString().trim()
        val anoStr = binding.anoEditText.text.toString().trim()
        val suporte = binding.suporteEditText.text.toString().trim()
        val localizacao = binding.localizacaoEditText.text.toString().trim()
        val situacao = binding.situacaoEditText.text.toString().trim()
        val sinopse = binding.sinopseEditText.text.toString().trim().takeIf { it.isNotEmpty() }

        if (registro.isEmpty() || isbn.isEmpty() || editora.isEmpty() || edicao.isEmpty()
            || anoStr.isEmpty() || suporte.isEmpty() || localizacao.isEmpty() || situacao.isEmpty()
        ) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val anoInt = anoStr.toIntOrNull()
        if (anoInt == null || anoInt > LocalDate.now().year) {
            Toast.makeText(this, "Ano inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        val anoPostgres = "$anoInt-01-01"

        val exemplar = Exemplar(
            id = exemplarToEdit?.id,
            livroId = livroId,
            registro = registro,
            isbn = isbn,
            editora = editora,
            edicao = edicao,
            ano = anoPostgres,
            suporte = suporte,
            localizacao = localizacao,
            situacao = situacao,
            sinopse = sinopse
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (exemplarToEdit == null) {
                    // CREATE
                    supabaseApi.registrarExemplar(
                        exemplar,
                        SupabaseConfig.SUPABASE_KEY,
                        "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                } else {
                    // UPDATE  -> id=eq.<id>
                    val id = exemplar.id
                    if (id == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddEditExemplarActivity,
                                "Erro: exemplar sem ID para edição.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return@launch
                    }

                    supabaseApi.atualizarExemplar(
                        idFilter = "eq.$id",
                        exemplar = exemplar,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddEditExemplarActivity,
                            "Exemplar salvo com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddEditExemplarActivity,
                            "Erro ao salvar exemplar: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddEditExemplarActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
