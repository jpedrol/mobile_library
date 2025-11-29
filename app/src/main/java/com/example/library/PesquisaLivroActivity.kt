package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class PesquisaActivity : AppCompatActivity() {

    private lateinit var recyclerLivros: RecyclerView
    private lateinit var adapter: BookAdapter
    private val listaLivros = mutableListOf<Book>()

    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_livro)

        // TOP BAR
        val btnVoltar     = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearch     = findViewById<ImageButton>(R.id.btnSearch)
        val txtPesquisa   = findViewById<EditText>(R.id.txtPesquisa)

        // ÁREA ADMIN
        val adminCrudContainer = findViewById<LinearLayout>(R.id.adminCrudContainer)
        val btnAddBook         = findViewById<Button>(R.id.btnAddBook)

        // Lista de livros
        recyclerLivros = findViewById(R.id.recyclerLivros)
        recyclerLivros.layoutManager = LinearLayoutManager(this)

        // Verifica se é admin
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val role = prefs.getString("role", "user")
        isAdmin = (role == "admin")

        // Mostra funções de admin apenas se for admin
        adminCrudContainer.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Botão adicionar livro (somente admin)
        btnAddBook.setOnClickListener {
            if (isAdmin) {
                startActivity(Intent(this, RegistrarLivroActivity::class.java))
            } else {
                Toast.makeText(this, "Acesso permitido somente para administradores.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão voltar
        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MenuInicialActivity::class.java))
            finish()
        }

        // Botão pesquisar
        btnSearch.setOnClickListener {
            val texto = txtPesquisa.text.toString().trim()
            if (texto.isNotEmpty()) {
                val intent = Intent(this, ResultadoPesquisaActivity::class.java)
                intent.putExtra("TERMO_PESQUISA", texto)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        // CONFIGURA O ADAPTER (agora com verificação de ADMIN)
        adapter = BookAdapter(
            listaLivros,
            isAdmin = isAdmin,  // <-- ENVIA PARA O ADAPTER
            onEdit = { livro, index ->
                if (isAdmin) {
                    val intent = Intent(this, EditarLivroActivity::class.java)
                    intent.putExtra("index", index)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Somente administradores podem editar livros.", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = { index ->
                if (isAdmin) {
                    excluirLivro(index)
                } else {
                    Toast.makeText(this, "Somente administradores podem excluir livros.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerLivros.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        carregarLivros()
    }

    private fun carregarLivros() {
        listaLivros.clear()

        val prefs = getSharedPreferences("books_db", MODE_PRIVATE)
        val json = prefs.getString("books_list", "[]")
        val arr = JSONArray(json)

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            listaLivros.add(
                Book(
                    title = obj.getString("title"),
                    author = obj.getString("author"),
                    language = obj.getString("language"),
                    coverUri = if (obj.has("coverUri")) obj.getString("coverUri") else null
                )
            )
        }

        adapter.notifyDataSetChanged()
    }

    private fun excluirLivro(index: Int) {
        val prefs = getSharedPreferences("books_db", MODE_PRIVATE)
        val json = prefs.getString("books_list", "[]")

        val arr = JSONArray(json)
        val newArr = JSONArray()

        for (i in 0 until arr.length()) {
            if (i != index) newArr.put(arr.getJSONObject(i))
        }

        prefs.edit().putString("books_list", newArr.toString()).apply()

        carregarLivros()
        Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show()
    }
}
