package com.example.library

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AvaliacaoLivroActivity : AppCompatActivity() {

    private lateinit var tvTituloAvaliacao: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var edtComentario: EditText
    private lateinit var btnEnviarAvaliacao: Button
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliacao_livro)

        tvTituloAvaliacao = findViewById(R.id.tvTituloAvaliacao)
        ratingBar = findViewById(R.id.ratingBar)
        edtComentario = findViewById(R.id.edtComentario)
        btnEnviarAvaliacao = findViewById(R.id.btnEnviarAvaliacao)
        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener { finish() }

        val titulo = intent.getStringExtra("LIVRO_TITULO") ?: ""
        tvTituloAvaliacao.text =
            if (titulo.isNotEmpty()) "Avaliar: $titulo" else "Livro não encontrado"

        btnEnviarAvaliacao.setOnClickListener {
            val estrelas = ratingBar.rating.toInt()
            val comentario = edtComentario.text.toString().trim()

            if (comentario.isEmpty()) {
                Toast.makeText(this, "Por favor, adicione um comentário.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nomeUsuario = SessionManager.getUserName(this)

            val avaliacao = Avaliacao(
                usuario = nomeUsuario,
                estrelas = estrelas,
                comentario = comentario
            )

            AvaliacaoStorage.salvarAvaliacao(this, titulo, avaliacao)

            Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
