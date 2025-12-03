package com.example.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import kotlin.random.Random

class ResultadoPesquisaActivity : AppCompatActivity() {

    private lateinit var imgLivroPrincipal: ImageView
    private lateinit var tvTituloPrincipal: TextView
    private lateinit var tvAutorPrincipal: TextView
    private lateinit var tvBaseadoEm: TextView

    private lateinit var imgLivroRecomendado: ImageView
    private lateinit var tvTituloRecomendado: TextView
    private lateinit var tvAutorRecomendado: TextView

    private lateinit var cardRecomendado: LinearLayout
    private lateinit var tvRecomendadoTitulo: TextView

    private var livroPrincipal: Book? = null
    private var livroRecomendado: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_pesquisa_livro)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearchRight = findViewById<ImageButton>(R.id.btnSearchRight)
        val txtPesquisa = findViewById<EditText>(R.id.txtPesquisa)
        val tvResultadoPara = findViewById<TextView>(R.id.tvResultadoPara)

        imgLivroPrincipal = findViewById(R.id.imgLivroPrincipal)
        tvTituloPrincipal = findViewById(R.id.tvTituloPrincipal)
        tvAutorPrincipal = findViewById(R.id.tvAutorPrincipal)
        tvBaseadoEm = findViewById(R.id.tvBaseadoEm)

        imgLivroRecomendado = findViewById(R.id.imgLivroRecomendado)
        tvTituloRecomendado = findViewById(R.id.tvTituloRecomendado)
        tvAutorRecomendado = findViewById(R.id.tvAutorRecomendado)

        cardRecomendado = findViewById(R.id.cardRecomendado)
        tvRecomendadoTitulo = findViewById(R.id.tvRecomendadoTitulo)

        val termo = intent.getStringExtra("TERMO_PESQUISA") ?: ""
        txtPesquisa.setText(termo)
        tvResultadoPara.text = "Resultado para: $termo"

        carregarLivrosParaTela(termo)

        btnVoltar.setOnClickListener { finish() }

        btnSearchRight.setOnClickListener {
            val novoTermo = txtPesquisa.text.toString().trim()

            if (novoTermo.isEmpty()) {
                Toast.makeText(this, "Digite algo para buscar 📚", Toast.LENGTH_SHORT).show()
            } else {
                val i = Intent(this, ResultadoPesquisaActivity::class.java)
                i.putExtra("TERMO_PESQUISA", novoTermo)
                startActivity(i)
                finish()
            }
        }

        imgLivroPrincipal.setOnClickListener {
            livroPrincipal?.let { livro ->
                abrirTelaDetalhe(livro)
            }
        }

        imgLivroRecomendado.setOnClickListener {
            livroRecomendado?.let { livro ->
                abrirTelaDetalhe(livro)
            }
        }
    }

    private fun abrirTelaDetalhe(livro: Book) {
        val i = Intent(this, TelaLivroDetalheActivity::class.java)
        i.putExtra("LIVRO_TITULO", livro.title)
        i.putExtra("LIVRO_AUTOR", livro.author)
        i.putExtra("LIVRO_IDIOMA", livro.language)
        i.putExtra("LIVRO_CAPA_URI", livro.coverUri)
        startActivity(i)
    }

    private fun carregarLivrosParaTela(termo: String) {
        val prefs = getSharedPreferences("books_db", MODE_PRIVATE)
        val json = prefs.getString("books_list", "[]")
        val arr = JSONArray(json)

        val todosLivros = mutableListOf<Book>()
        val resultados = mutableListOf<Book>()

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)

            val livro = Book(
                title = obj.getString("title"),
                author = obj.getString("author"),
                language = obj.getString("language"),
                coverUri = if (obj.has("coverUri")) obj.getString("coverUri") else null
            )

            todosLivros.add(livro)

            if (
                livro.title.contains(termo, ignoreCase = true) ||
                livro.author.contains(termo, ignoreCase = true) ||
                livro.language.contains(termo, ignoreCase = true)
            ) {
                resultados.add(livro)
            }
        }

        if (resultados.isEmpty()) {
            Toast.makeText(this, "Nenhum livro encontrado para \"$termo\"", Toast.LENGTH_SHORT).show()
            findViewById<LinearLayout>(R.id.cardPrincipal).visibility = View.GONE
            cardRecomendado.visibility = View.GONE
            tvRecomendadoTitulo.visibility = View.GONE
            return
        }

        livroPrincipal = resultados.first()
        preencherLivroPrincipal(livroPrincipal!!, termo)

        val candidatos = todosLivros.filter { it != livroPrincipal }

        if (candidatos.isEmpty()) {
            cardRecomendado.visibility = View.GONE
            tvRecomendadoTitulo.visibility = View.GONE
        } else {
            livroRecomendado = candidatos.random()
            preencherLivroRecomendado(livroRecomendado!!)
        }
    }

    private fun preencherLivroPrincipal(livro: Book, termo: String) {
        tvTituloPrincipal.text = livro.title
        tvAutorPrincipal.text = livro.author
        tvBaseadoEm.text = "Baseado em: $termo"

        if (!livro.coverUri.isNullOrEmpty()) {
            imgLivroPrincipal.setImageURI(Uri.parse(livro.coverUri))
        } else {
            imgLivroPrincipal.setImageResource(R.drawable.ic_book_placeholder)
        }
    }

    private fun preencherLivroRecomendado(livro: Book) {
        tvTituloRecomendado.text = livro.title
        tvAutorRecomendado.text = livro.author

        if (!livro.coverUri.isNullOrEmpty()) {
            imgLivroRecomendado.setImageURI(Uri.parse(livro.coverUri))
        } else {
            imgLivroRecomendado.setImageResource(R.drawable.ic_book_placeholder)
        }
    }
}
