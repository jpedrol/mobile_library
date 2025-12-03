package com.example.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditarLivroActivity : AppCompatActivity() {

    private val supabaseApi = SupabaseClient.api

    private var capaUriSelecionada: Uri? = null
    private lateinit var imgPreview: ImageView
    private var livroId = -1
    private var capaUrlAtual: String? = null

    private val selecionarImagemLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                capaUriSelecionada = uri
                imgPreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SessionManager.isAdmin(this)) {
            Toast.makeText(
                this,
                "Acesso negado. Apenas administradores podem editar livros.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContentView(R.layout.activity_registrar_livro)

        val inputTitulo = findViewById<EditText>(R.id.inputTitulo)
        val inputAutor = findViewById<EditText>(R.id.inputAutor)
        val inputIdioma = findViewById<EditText>(R.id.inputIdioma)
        val btnAddCapa = findViewById<FrameLayout>(R.id.btnAddCapa)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarLivro)
        imgPreview = findViewById(R.id.imgPreview)

        findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener { finish() }

        livroId = intent.getIntExtra("LIVRO_ID", -1)
        if (livroId == -1) {
            Toast.makeText(this, "Erro: ID do livro não fornecido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val btnGerenciarExemplares = findViewById<Button>(R.id.btnGerenciarExemplares)
        btnGerenciarExemplares.visibility = Button.VISIBLE
        btnRegistrar.text = "Salvar alterações"


        carregarLivro(livroId, inputTitulo, inputAutor, inputIdioma)

        btnGerenciarExemplares.setOnClickListener {
            val intent = Intent(this, ExemplaresAdminActivity::class.java).apply {
                putExtra("LIVRO_ID", livroId)
                putExtra("LIVRO_TITULO", inputTitulo.text.toString())
            }
            startActivity(intent)
        }

        btnAddCapa.setOnClickListener {
            selecionarImagemLauncher.launch("image/*")
        }

        btnRegistrar.setOnClickListener {
            val titulo = inputTitulo.text.toString().trim()
            val autor = inputAutor.text.toString().trim()
            val idioma = inputIdioma.text.toString().trim()

            if (titulo.isEmpty() || autor.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    btnRegistrar.isEnabled = false


                    val novaCapaUrl = capaUriSelecionada?.let {
                        uploadCapaFake(it)
                    } ?: capaUrlAtual

                    val livroAtualizado = Livro(
                        id = livroId,
                        titulo = titulo,
                        autores = autor,
                        idiomas = idioma,
                        capaUrl = novaCapaUrl
                    )

                    val response = supabaseApi.atualizarLivro(
                        idFilter = "eq.$livroId",
                        livro = livroAtualizado,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditarLivroActivity,
                            "Livro atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditarLivroActivity,
                            "Erro ao atualizar livro: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@EditarLivroActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnRegistrar.isEnabled = true
                }
            }
        }
    }

    private fun carregarLivro(
        id: Int,
        inputTitulo: EditText,
        inputAutor: EditText,
        inputIdioma: EditText
    ) {
        lifecycleScope.launch {
            try {
                val response = supabaseApi.getLivroPorId(
                    idFilter = "eq.$id",                              // 🔑 filtro correto
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                val lista = response.body()

                if (response.isSuccessful && lista != null && lista.isNotEmpty()) {
                    val livro = lista[0]
                    inputTitulo.setText(livro.titulo)
                    inputAutor.setText(livro.autores)
                    inputIdioma.setText(livro.idiomas)
                    capaUrlAtual = livro.capaUrl

                } else {
                    Toast.makeText(
                        this@EditarLivroActivity,
                        "Erro ao carregar livro: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@EditarLivroActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }


    private fun uploadCapaFake(uri: Uri): String {
        return "https://placehold.co/400x600?text=Capa+Livro"
    }
}
