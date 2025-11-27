package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TelaLivroDetalheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_detalhe)

        val btnVoltar            = findViewById<ImageButton>(R.id.btnVoltar)

        val tvTituloLivro        = findViewById<TextView>(R.id.tvTituloLivro)
        val tvAvaliacaoNumero    = findViewById<TextView>(R.id.tvAvaliacaoNumero)
        val tvAvaliacaoEstrelas  = findViewById<TextView>(R.id.tvAvaliacaoEstrelas)
        val tvQtdAvaliacoes      = findViewById<TextView>(R.id.tvQtdAvaliacoes)
        val imgCapaLivro         = findViewById<ImageView>(R.id.imgCapaLivro)
        val tvDataLancamento     = findViewById<TextView>(R.id.tvDataLancamento)
        val tvClassificacaoIdade = findViewById<TextView>(R.id.tvClassificacaoIdade)
        val tvQtdPaginas         = findViewById<TextView>(R.id.tvQtdPaginas)

        // Grid "Mais sobre"
        val tvDescricaoLivro     = findViewById<TextView>(R.id.tvDescricaoLivro)
        val tvQtdExemplares      = findViewById<TextView>(R.id.tvQtdExemplares)
        val btnAlugar            = findViewById<Button>(R.id.btnAlugar)

        // Grid "Avaliações de usuários"
        val tvNomeUsuario        = findViewById<TextView>(R.id.tvNomeUsuario)
        val tvEstrelasUsuario    = findViewById<TextView>(R.id.tvEstrelasUsuario)
        val tvComentarioUsuario  = findViewById<TextView>(R.id.tvComentarioUsuario)
        val tvDataAvaliacaoUser  = findViewById<TextView>(R.id.tvDataAvaliacaoUsuario)
        val btnAvaliarLivro      = findViewById<Button>(R.id.btnAvaliarLivro)

        // ========= RECEBE DADOS DO INTENT =========
        val titulo       = intent.getStringExtra("LIVRO_TITULO") ?: "Título do livro"
        val avaliacaoNum = intent.getFloatExtra("LIVRO_AVALIACAO_NUM", 0f)
        val qtdAval      = intent.getIntExtra("LIVRO_QTD_AVALIACOES", 0)
        val dataLanc     = intent.getStringExtra("LIVRO_DATA_LANC") ?: ""
        val classif      = intent.getStringExtra("LIVRO_CLASSIFICACAO") ?: ""
        val qtdPag       = intent.getIntExtra("LIVRO_QTD_PAGINAS", 0)
        val qtdExemp     = intent.getIntExtra("LIVRO_QTD_EXEMPLARES", 0)
        val descricao    = intent.getStringExtra("LIVRO_DESCRICAO") ?: ""
        val capaResId    = intent.getIntExtra("LIVRO_CAPA_RES_ID", R.drawable.livro1)

        tvTituloLivro.text       = titulo
        tvAvaliacaoNumero.text   = String.format("%.1f", avaliacaoNum)
        tvAvaliacaoEstrelas.text = "★★★★★" // depois você pode gerar dinamicamente
        tvQtdAvaliacoes.text     = "($qtdAval)"
        imgCapaLivro.setImageResource(capaResId)

        tvDataLancamento.text    = dataLanc
        tvClassificacaoIdade.text= classif
        tvQtdPaginas.text        = "$qtdPag páginas."

        tvDescricaoLivro.text    = descricao
        tvQtdExemplares.text     = "Quantidade de exemplares: $qtdExemp"

        tvNomeUsuario.text       = "Gabriel Gomes"
        tvEstrelasUsuario.text   = "★★★★★"
        tvComentarioUsuario.text = "Livro incrível, muito emocionante!"
        tvDataAvaliacaoUser.text = "Avaliado em: 10/11/2025"

        btnVoltar.setOnClickListener {
            finish()
        }

        btnAlugar.setOnClickListener {
            Toast.makeText(this, "Fluxo de aluguel ainda será implementado.", Toast.LENGTH_SHORT).show()
        }


        btnAvaliarLivro.setOnClickListener {
            val titulo = "A culpa é das estrelas" // Substitua isso pelo dado real, que pode vir de um banco de dados ou modelo

            val intent = Intent(this, AvaliacaoLivroActivity::class.java)
            intent.putExtra("LIVRO_TITULO", titulo) // Passando o título para a tela de avaliação

            startActivity(intent)
        }
    }
}
