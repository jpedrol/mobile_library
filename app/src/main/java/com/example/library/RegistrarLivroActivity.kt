package com.example.library

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class RegistrarLivroActivity : AppCompatActivity() {

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

            val caminhoLocal = capaUriSelecionada?.let { salvarImagemLocal(it) }

            val prefs = getSharedPreferences("books_db", MODE_PRIVATE)
            val arr = JSONArray(prefs.getString("books_list", "[]"))

            val novoObj = JSONObject()
            novoObj.put("title", titulo)
            novoObj.put("author", autor)
            novoObj.put("language", idioma)
            novoObj.put("coverUri", caminhoLocal ?: JSONObject.NULL)

            arr.put(novoObj)
            prefs.edit().putString("books_list", arr.toString()).apply()

            LivroRepository.livros.add(
                Book(titulo, autor, idioma, caminhoLocal)
            )

            Toast.makeText(this, "Livro registrado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun salvarImagemLocal(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null

            val dir = File(filesDir, "book_covers")
            if (!dir.exists()) dir.mkdirs()

            val nomeArquivo = "capa_${System.currentTimeMillis()}.png"
            val arquivoDestino = File(dir, nomeArquivo)

            val output = FileOutputStream(arquivoDestino)
            input.copyTo(output)

            input.close()
            output.close()

            arquivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
