package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultadoPesquisaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_pesquisa_livro)

        val btnVoltar           = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearchRight      = findViewById<ImageButton>(R.id.btnSearchRight)
        val txtPesquisa         = findViewById<EditText>(R.id.txtPesquisa)
        val tvResultadoPara     = findViewById<TextView>(R.id.tvResultadoPara)
        val tvBaseadoEm         = findViewById<TextView>(R.id.tvBaseadoEm)
        val imgLivroPrincipal   = findViewById<ImageView>(R.id.imgLivroPrincipal)
        val imgLivroRecomendado = findViewById<ImageView>(R.id.imgLivroRecomendado)

        // Recuperando termo vindo da pesquisa
        val termo = intent.getStringExtra("TERMO_PESQUISA") ?: ""

        txtPesquisa.setText(termo)
        tvResultadoPara.text = "Resultado para: $termo."
        tvBaseadoEm.text     = "Baseado em: $termo."

        // Botão voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // Lupa da direita = pesquisar novamente
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

        // Clicou no livro principal → abre detalhes
        imgLivroPrincipal.setOnClickListener {
            val i = Intent(this, TelaLivroDetalheActivity::class.java)
            startActivity(i)
        }

        // Clicou no recomendado → abre detalhes também
        imgLivroRecomendado.setOnClickListener {
            val i = Intent(this, TelaLivroDetalheActivity::class.java)
            startActivity(i)
        }
    }
}
