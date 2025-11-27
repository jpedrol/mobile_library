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

        val btnVoltar          = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearch          = findViewById<ImageButton>(R.id.btnSearch)
        val btnExcluir         = findViewById<ImageButton>(R.id.btnExcluir)
        val txtPesquisa        = findViewById<EditText>(R.id.txtPesquisa)
        val tvResultadoPara    = findViewById<TextView>(R.id.tvResultadoPara)
        val tvBaseadoEm        = findViewById<TextView>(R.id.tvBaseadoEm)
        val imgLivroPrincipal  = findViewById<ImageView>(R.id.imgLivroPrincipal)
        val imgLivroRecomendado= findViewById<ImageView>(R.id.imgLivroRecomendado)

        val termo = intent.getStringExtra("TERMO_PESQUISA") ?: ""

        txtPesquisa.setText(termo)
        tvResultadoPara.text = "Resultado para: $termo."
        tvBaseadoEm.text     = "Baseado em: $termo."

        btnVoltar.setOnClickListener {
            finish()
        }

        btnSearch.setOnClickListener {
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

        btnExcluir.setOnClickListener {
            val i = Intent(this, PesquisaActivity::class.java)
            startActivity(i)
            finish()
        }

        imgLivroPrincipal.setOnClickListener {
            val i = Intent(this, TelaLivroDetalheActivity::class.java)
            startActivity(i)
        }

        imgLivroRecomendado.setOnClickListener {
            val i = Intent(this, TelaLivroDetalheActivity::class.java)
            startActivity(i)
        }
    }
}
