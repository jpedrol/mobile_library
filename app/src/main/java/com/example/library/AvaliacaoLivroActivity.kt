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
    private lateinit var btnVoltar: ImageButton   // <-- ADICIONADO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliacao_livro)

        // Referências aos componentes da UI
        tvTituloAvaliacao = findViewById(R.id.tvTituloAvaliacao)
        ratingBar = findViewById(R.id.ratingBar)
        edtComentario = findViewById(R.id.edtComentario)
        btnEnviarAvaliacao = findViewById(R.id.btnEnviarAvaliacao)
        btnVoltar = findViewById(R.id.btnVoltar)  // <-- AGORA FUNCIONA

        // Botão voltar: volta para a tela anterior (TelaLivroDetalhe)
        btnVoltar.setOnClickListener {
            finish()
        }

        // Recupera o título do livro passado pela Intent
        val titulo = intent.getStringExtra("LIVRO_TITULO")
        if (titulo != null) {
            tvTituloAvaliacao.text = "Avaliar: $titulo"
        } else {
            tvTituloAvaliacao.text = "Livro não encontrado"
        }

        // Ação ao clicar no botão de enviar
        btnEnviarAvaliacao.setOnClickListener {
            val estrelas = ratingBar.rating
            val comentario = edtComentario.text.toString()

            if (comentario.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Avaliação enviada: $estrelas estrelas, comentário: $comentario",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {
                Toast.makeText(
                    this,
                    "Por favor, adicione um comentário.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
