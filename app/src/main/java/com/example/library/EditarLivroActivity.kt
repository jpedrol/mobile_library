package com.example.library

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class EditarLivroActivity : AppCompatActivity() {

    private var capaUriSelecionada: Uri? = null
    private lateinit var imgPreview: ImageView
    private var indexLivro = -1

    private val selecionarImagemLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                capaUriSelecionada = uri
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

        findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener { finish() }

        indexLivro = intent.getIntExtra("index", -1)
        if (indexLivro == -1) { finish(); return }

        val prefs = getSharedPreferences("books_db", MODE_PRIVATE)
        val arr = JSONArray(prefs.getString("books_list", "[]"))
        val obj = arr.getJSONObject(indexLivro)

        inputTitulo.setText(obj.getString("title"))
        inputAutor.setText(obj.getString("author"))
        inputIdioma.setText(obj.getString("language"))

        val capa = obj.optString("coverUri", null)
        if (!capa.isNullOrEmpty()) {
            imgPreview.setImageURI(Uri.parse(capa))
            imgPreview.visibility = ImageView.VISIBLE
        }

        btnAddCapa.setOnClickListener {
            selecionarImagemLauncher.launch("image/*")
        }

        btnRegistrar.text = "Salvar alterações"

        btnRegistrar.setOnClickListener {
            val titulo = inputTitulo.text.toString()
            val autor = inputAutor.text.toString()
            val idioma = inputIdioma.text.toString()

            val caminhoCapa = capaUriSelecionada?.let { salvarImagemLocal(it) }
                ?: capa

            val novo = obj
            novo.put("title", titulo)
            novo.put("author", autor)
            novo.put("language", idioma)
            novo.put("coverUri", caminhoCapa)

            arr.put(indexLivro, novo)
            prefs.edit().putString("books_list", arr.toString()).apply()

            LivroRepository.livros[indexLivro] =
                Book(titulo, autor, idioma, caminhoCapa)

            Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun salvarImagemLocal(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null

            val dir = File(filesDir, "book_covers")
            if (!dir.exists()) dir.mkdirs()

            val nome = "capa_${System.currentTimeMillis()}.png"
            val dest = File(dir, nome)

            val output = FileOutputStream(dest)
            input.copyTo(output)

            input.close()
            output.close()
            dest.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
