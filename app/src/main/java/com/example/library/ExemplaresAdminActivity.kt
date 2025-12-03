package com.example.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.databinding.ActivityExemplaresAdminBinding
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExemplaresAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExemplaresAdminBinding
    private lateinit var exemplarAdapter: ExemplarAdapter
    private val exemplaresList = mutableListOf<Exemplar>()

    // Usa SupabaseClient do mesmo package
    private val supabaseApi = SupabaseClient.api

    private var livroId: Int = -1
    private var livroTitulo: String = ""

    private val addEditExemplarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadExemplares()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExemplaresAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        livroId = intent.getIntExtra("LIVRO_ID", -1)
        livroTitulo = intent.getStringExtra("LIVRO_TITULO") ?: "Livro Desconhecido"

        if (livroId == -1) {
            Toast.makeText(this, "ID do Livro não fornecido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViews()
        setupRecyclerView()
        loadExemplares()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener { finish() }
        binding.bookTitleTextView.text = "Livro: $livroTitulo"

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddEditExemplarActivity::class.java).apply {
                putExtra("LIVRO_ID", livroId)
            }
            addEditExemplarLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        exemplarAdapter = ExemplarAdapter(
            exemplaresList,
            onEditClick = { exemplar ->
                val intent = Intent(this, AddEditExemplarActivity::class.java).apply {
                    putExtra("LIVRO_ID", livroId)
                    putExtra("EXEMPLAR_EDITAR", exemplar) // Exemplar é Serializable
                }
                addEditExemplarLauncher.launch(intent)
            },
            onDeleteClick = { exemplar ->
                confirmDelete(exemplar)
            }
        )

        binding.exemplaresRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExemplaresAdminActivity)
            adapter = exemplarAdapter
        }
    }

    private fun loadExemplares() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.getExemplaresPorLivro(
                    livroIdFilter = "eq.$livroId",
                    select = "*",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        exemplarAdapter.updateList(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@ExemplaresAdminActivity,
                            "Erro ao carregar exemplares: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ExemplaresAdminActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun confirmDelete(exemplar: Exemplar) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Exemplar")
            .setMessage("Tem certeza que deseja excluir o exemplar de registro: ${exemplar.registro}?")
            .setPositiveButton("Excluir") { _, _ ->
                deleteExemplar(exemplar)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteExemplar(exemplar: Exemplar) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.deletarExemplar(
                    idFilter = "eq.${exemplar.id}",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        exemplarAdapter.removeItem(exemplar)
                        Toast.makeText(
                            this@ExemplaresAdminActivity,
                            "Exemplar excluído com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ExemplaresAdminActivity,
                            "Erro ao excluir exemplar: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ExemplaresAdminActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
