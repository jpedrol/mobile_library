package com.example.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.launch

class RegistrarLivroActivity : AppCompatActivity() {

    private val supabaseApi = SupabaseClient.api

    private var capaUriSelecionada: Uri? = null
    private lateinit var imgPreview: ImageView

    private val selecionarImagemLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                capaUriSelecionada = uri
                // mostra a capa escolhida
                imgPreview.visibility = ImageView.VISIBLE
                imgPreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SessionManager.isAdmin(this)) {
            Toast.makeText(this, "Acesso negado. Apenas administradores podem registrar livros.", Toast.LENGTH_LONG).show()
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

        findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            finish()
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

                    // Simulação de upload de imagem para o Supabase Storage
                    val capaUrl = capaUriSelecionada?.let { uploadCapa(it) } ?: ""

                    val novoLivro = Livro(
                        titulo = titulo,
                        autores = autor,
                        idiomas = idioma,
                        capaUrl = capaUrl
                    )

                    val response = supabaseApi.registrarLivro(
                        novoLivro,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )

                    if (response.isSuccessful) {
                        val livroRegistrado = response.body()?.firstOrNull()
                        if (livroRegistrado != null) {
                            Toast.makeText(this@RegistrarLivroActivity, "Livro registrado com sucesso! Cadastre o primeiro exemplar.", Toast.LENGTH_LONG).show()
                            val intent = Intent(
                                this@RegistrarLivroActivity,
                                AddEditExemplarActivity::class.java
                            ).apply {
                                putExtra("LIVRO_ID", livroRegistrado.id)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegistrarLivroActivity, "Livro registrado, mas ID não retornado. Tente cadastrar o exemplar manualmente.", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@RegistrarLivroActivity, "Erro ao registrar livro: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegistrarLivroActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnRegistrar.isEnabled = true
                }
            }
        }
    }

    // Função placeholder para simular o upload para o Supabase Storage
    private fun uploadCapa(uri: Uri): String {
        // Em um projeto real, aqui você faria o upload para o Supabase Storage
        // e retornaria a URL pública.
        // Como não temos acesso ao Supabase Storage, retornamos uma URL de placeholder.
        return "https://placehold.co/400x600?text=Capa+Livro"
    }
}
